package com.framex.app.ui.screens.performance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.framex.app.device.DeviceDiagnosticManager
import com.framex.app.gaming.AppInfo
import com.framex.app.gaming.GamingModeEngine
import com.framex.app.gaming.GamingModeState
import com.framex.app.gaming.GamingPlatformPath
import com.framex.app.gaming.GamingServiceController
import com.framex.app.gaming.SystemAuditLog
import com.framex.app.gaming.VivoGamingOptimizer
import com.framex.app.gaming.VivoSuiteGate
import com.framex.app.gaming.ledger.ExecutionLedger
import com.framex.app.metrics.MetricsEngine
import com.framex.app.repository.SettingsRepository
import com.framex.app.shizuku.ShizukuManager
import com.framex.app.utils.FrameXLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val gamingModeEngine: GamingModeEngine,
    private val vivoGamingOptimizer: VivoGamingOptimizer,
    private val shizukuManager: ShizukuManager,
    private val settingsRepository: SettingsRepository,
    private val metricsEngine: MetricsEngine,
    private val deviceDiagnosticManager: DeviceDiagnosticManager,
    private val executionLedger: ExecutionLedger,
    private val vivoSuiteGate: VivoSuiteGate,
    private val gamingServiceController: GamingServiceController
) : ViewModel() {

    private val _effectChannel = Channel<PerformanceUiEffect>(Channel.BUFFERED)
    val effect = _effectChannel.receiveAsFlow()

    val maxRefreshRate: Int = deviceDiagnosticManager.getMaxHardwareRefreshRate().toInt().coerceAtLeast(60)

    // Dynamic state streams
    private val _userApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val userApps = _userApps.asStateFlow()

    private val _googleApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val googleApps = _googleApps.asStateFlow()

    private val _rawPerfGameList = MutableStateFlow<String?>(null)
    val rawPerfGameList = _rawPerfGameList.asStateFlow()

    private val _vivoPerfGameList = MutableStateFlow<List<String>>(emptyList())
    val vivoPerfGameList = _vivoPerfGameList.asStateFlow()

    // Banner message state
    private val _bannerMessage = MutableStateFlow<String?>(null)

    // Dialog & Modal visibility states
    private val _showAddGameSheet = MutableStateFlow(false)
    private val _configGamePkg = MutableStateFlow<String?>(null)
    private val _activeDeployingGamePkg = MutableStateFlow<String?>(null)

    // System Access state
    private val _hasDndAccess = MutableStateFlow(deviceDiagnosticManager.hasDndAccess())
    private val _hasNotifListenerAccess = MutableStateFlow(deviceDiagnosticManager.hasNotificationListenerAccess())
    private val _hasWriteSettingsAccess = MutableStateFlow(deviceDiagnosticManager.hasWriteSettingsAccess())

    private val systemAccessStream = combine(
        _hasDndAccess,
        _hasNotifListenerAccess,
        _hasWriteSettingsAccess
    ) { dnd, notif, writeSettings ->
        SystemAccessGroup(dnd, notif, writeSettings)
    }

    // Active session stream
    val activeGamingSession: StateFlow<ActiveGamingSession?> = combine(
        gamingModeEngine.state,
        gamingModeEngine.activeGamePackage,
        gamingModeEngine.suspendedPackagesCount,
        executionLedger.ops
    ) { state, activePkg, suspendedCount, _ ->
        if (state !is GamingModeState.Active) {
            null
        } else {
            ActiveGamingSession(
                title = "Gaming Mode Active",
                isVivoDevice = vivoSuiteGate.isVivoHardware,
                activeGamePackage = activePkg,
                suspendedAppsCount = suspendedCount,
                summary = executionLedger.getSummary()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // Grouped streams to stay strictly within combine limits
    private val shizukuAndGamingStream = combine(
        gamingModeEngine.state,
        shizukuManager.isShizukuAvailable,
        shizukuManager.hasPermission,
        settingsRepository.gamingModeWhitelist,
        settingsRepository.launcherGames
    ) { state, available, perm, wl, games ->
        ShizukuAndGamingGroup(state, available, perm, wl, games)
    }

    private val systemSettingsStream = combine(
        settingsRepository.fixedPerformanceMode,
        settingsRepository.deepFreezeEnabled,
        settingsRepository.hasSeenDeepFreezeNotice,
        settingsRepository.auditLoggingEnabled
    ) { fixedPerf, deepFreeze, hasSeenNotice, auditEnabled ->
        SystemSettingsGroup(fixedPerf, deepFreeze, hasSeenNotice, auditEnabled)
    }

    private val vivoStream = combine(
        vivoSuiteGate.isVivoSuiteEnabledFlow,
        _rawPerfGameList,
        _vivoPerfGameList,
        vivoGamingOptimizer.auditLogs
    ) { enabled, raw, list, logs ->
        VivoGroup(enabled, raw, list, logs)
    }

    private val dialogStateStream = combine(
        _showAddGameSheet,
        _configGamePkg,
        _activeDeployingGamePkg
    ) { addGame, configPkg, deployingPkg ->
        DialogStateGroup(addGame, configPkg, deployingPkg)
    }

    val uiState: StateFlow<PerformanceUiState> = combine(
        shizukuAndGamingStream,
        systemSettingsStream,
        vivoStream,
        metricsEngine.metricsState,
        combine(
            _userApps,
            _googleApps,
            activeGamingSession,
            _bannerMessage,
            combine(dialogStateStream, systemAccessStream) { dialogs, access ->
                Pair(dialogs, access)
            }
        ) { user, google, session, banner, (dialogs, access) ->
            IntermediateUiState(user, google, session, banner, dialogs, access)
        }
    ) { sg, sys, vivo, metrics, inter ->
        PerformanceUiState(
            gamingState = sg.gamingState,
            isShizukuAvailable = sg.isShizukuAvailable,
            hasShizukuPermission = sg.hasShizukuPermission,
            whitelist = sg.whitelist,
            launcherGames = sg.launcherGames,
            userApps = inter.userApps,
            googleApps = inter.googleApps,
            metricsState = metrics,
            fixedPerformanceMode = sys.fixedPerformanceMode,
            deepFreezeEnabled = sys.deepFreezeEnabled,
            hasSeenDeepFreezeNotice = sys.hasSeenDeepFreezeNotice,
            activeGamingSession = inter.activeSession,
            isVivoSuiteEnabled = vivo.isVivoSuiteEnabled,
            rawPerfGameList = vivo.rawPerfGameList,
            vivoPerfGameList = vivo.vivoPerfGameList,
            vivoAuditLogs = vivo.vivoAuditLogs,
            auditLoggingEnabled = sys.auditLoggingEnabled,
            maxRefreshRate = maxRefreshRate,
            hasDndAccess = inter.systemAccess.hasDndAccess,
            hasNotifListenerAccess = inter.systemAccess.hasNotifListenerAccess,
            hasWriteSettingsAccess = inter.systemAccess.hasWriteSettingsAccess,
            bannerMessage = inter.bannerMessage,
            showAddGameSheet = inter.dialogs.showAddGameSheet,
            configGamePkg = inter.dialogs.configGamePkg,
            activeDeployingGamePkg = inter.dialogs.activeDeployingGamePkg
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), createInitialUiState())

    private fun createInitialUiState(): PerformanceUiState {
        val vivoEnabled = vivoSuiteGate.isVivoSuiteEnabled
        val activeSession = if (gamingModeEngine.state.value is GamingModeState.Active) {
            ActiveGamingSession(
                title = "Gaming Mode Active",
                isVivoDevice = vivoSuiteGate.isVivoHardware,
                activeGamePackage = gamingModeEngine.activeGamePackage.value,
                suspendedAppsCount = gamingModeEngine.suspendedPackagesCount.value,
                summary = executionLedger.getSummary()
            )
        } else null

        return PerformanceUiState(
            gamingState = gamingModeEngine.state.value,
            isShizukuAvailable = shizukuManager.isShizukuAvailable.value,
            hasShizukuPermission = shizukuManager.hasPermission.value,
            whitelist = settingsRepository.gamingModeWhitelist.value,
            launcherGames = settingsRepository.launcherGames.value,
            userApps = emptyList(),
            googleApps = emptyList(),
            metricsState = metricsEngine.metricsState.value,
            fixedPerformanceMode = settingsRepository.fixedPerformanceMode.value,
            deepFreezeEnabled = settingsRepository.deepFreezeEnabled.value,
            hasSeenDeepFreezeNotice = settingsRepository.hasSeenDeepFreezeNotice.value,
            activeGamingSession = activeSession,
            isVivoSuiteEnabled = vivoEnabled,
            rawPerfGameList = null,
            vivoPerfGameList = emptyList(),
            vivoAuditLogs = emptyList(),
            auditLoggingEnabled = settingsRepository.auditLoggingEnabled.value,
            maxRefreshRate = maxRefreshRate,
            hasDndAccess = _hasDndAccess.value,
            hasNotifListenerAccess = _hasNotifListenerAccess.value,
            hasWriteSettingsAccess = _hasWriteSettingsAccess.value,
            bannerMessage = null,
            showAddGameSheet = false,
            configGamePkg = null,
            activeDeployingGamePkg = null
        )
    }

    init {
        loadUserApps()
        refreshSystemState()
        metricsEngine.setScreenOverrideModules(setOf("cpu", "ram", "fps"), requesterKey = "performance_screen")
    }

    override fun onCleared() {
        super.onCleared()
        metricsEngine.setScreenOverrideModules(emptySet(), requesterKey = "performance_screen")
    }

    fun refreshSystemState() {
        viewModelScope.launch(Dispatchers.IO) {
            _hasDndAccess.value = deviceDiagnosticManager.hasDndAccess()
            _hasNotifListenerAccess.value = deviceDiagnosticManager.hasNotificationListenerAccess()
            _hasWriteSettingsAccess.value = deviceDiagnosticManager.hasWriteSettingsAccess()
        }
    }

    fun onEvent(event: PerformanceUiEvent) {
        when (event) {
            is PerformanceUiEvent.ToggleWhitelist -> settingsRepository.toggleGamingWhitelistApp(event.packageName)
            is PerformanceUiEvent.ToggleLauncherGame -> settingsRepository.toggleLauncherGame(event.packageName)
            is PerformanceUiEvent.ToggleDeepFreeze -> settingsRepository.setDeepFreezeEnabled(event.enabled)
            PerformanceUiEvent.DismissDeepFreezeNotice -> settingsRepository.setHasSeenDeepFreezeNotice(true)
            PerformanceUiEvent.EnableGamingMode -> enableGamingMode()
            PerformanceUiEvent.DisableGamingMode -> disableGamingMode()
            is PerformanceUiEvent.LaunchGame -> launchGameWithOptimizations(event.packageName)
            PerformanceUiEvent.RefreshVivoPerfList -> refreshVivoPerfGameList()
            is PerformanceUiEvent.AddAllToPerfList -> addAllLauncherGamesToPerfList(event.packages, event.onComplete)
            is PerformanceUiEvent.RemoveAllFromPerfList -> removeAllLauncherGamesFromPerfList(event.packages, event.onComplete)
            is PerformanceUiEvent.CompileAllSpeed -> compileAllLauncherGamesSpeed(event.packages, event.onComplete)
            is PerformanceUiEvent.ToggleAuditLogging -> setAuditLoggingEnabled(event.enabled)
            PerformanceUiEvent.ClearAuditLogs -> clearVivoAuditLogs()
            PerformanceUiEvent.RefreshInstalledApps -> loadUserApps()
            PerformanceUiEvent.RefreshSystemState -> {
                loadUserApps()
                refreshSystemState()
            }
            is PerformanceUiEvent.SetAddGameSheetVisible -> _showAddGameSheet.value = event.visible
            is PerformanceUiEvent.SetConfigGamePkg -> _configGamePkg.value = event.packageName
            is PerformanceUiEvent.SetDeployingGamePkg -> _activeDeployingGamePkg.value = event.packageName
            is PerformanceUiEvent.SetGameConfigBoostRam -> settingsRepository.setGameConfigBoostRam(event.packageName, event.enabled)
            is PerformanceUiEvent.ToggleMemc -> toggleMemc(event.packageName, event.enabled, event.onComplete)
        }
    }

    fun loadUserApps() {
        viewModelScope.launch {
            val installedUser = withContext(Dispatchers.IO) {
                gamingModeEngine.getInstalledUserApps()
            }
            val installedGoogle = withContext(Dispatchers.IO) {
                gamingModeEngine.getGoogleAppsForWhitelist()
            }
            _userApps.value = installedUser
            _googleApps.value = installedGoogle

            val installedPkgs = installedUser.map { it.packageName }.toSet()
            if (installedPkgs.isNotEmpty()) {
                val currentLauncher = settingsRepository.launcherGames.value
                val validLauncher = currentLauncher.filter { it in installedPkgs }.toSet()
                if (validLauncher.size != currentLauncher.size) {
                    settingsRepository.setLauncherGames(validLauncher)
                }
            }
        }
    }

    fun enableGamingMode() {
        if (settingsRepository.launcherGames.value.isEmpty()) {
            _effectChannel.trySend(PerformanceUiEffect.ShowToast("Kindly add a minimum of one game in game launcher"))
            return
        }
        viewModelScope.launch {
            val currentWhitelist = settingsRepository.gamingModeWhitelist.value
            gamingModeEngine.enableGamingMode(currentWhitelist)
            if (gamingModeEngine.state.value == GamingModeState.Active) {
                gamingServiceController.startGamingService()
                if (settingsRepository.getGamingPlatformPath() == GamingPlatformPath.VIVO) {
                    _effectChannel.send(PerformanceUiEffect.ShowToast("Gaming Mode active: Launch your game within 2 min for PID-locked performance optimizations."))
                }
            }
        }
    }

    fun disableGamingMode() {
        viewModelScope.launch {
            gamingModeEngine.disableGamingMode()
            gamingServiceController.stopGamingService()
        }
    }

    fun launchGameWithOptimizations(packageName: String, onLaunched: ((Long) -> Unit)? = null) {
        viewModelScope.launch {
            val isGamingModeActive = gamingModeEngine.state.value is GamingModeState.Active
            val shouldBoostRam = settingsRepository.getGameConfigBoostRam(packageName)

            var freedMb = 0L
            if (shouldBoostRam) {
                val currentWhitelist = settingsRepository.gamingModeWhitelist.value
                val (freed, _) = manualBoostRam(currentWhitelist + packageName)
                // Critical Fix: manualBoostRam already returns freed in MB, do not divide by 1024*1024 again!
                freedMb = freed.coerceAtLeast(0L)
            }

            val launched = gamingServiceController.launchApp(packageName)
            if (launched) {
                if (isGamingModeActive) {
                    val sessionPath = settingsRepository.getGamingPlatformPath()
                    if (sessionPath == GamingPlatformPath.VIVO) {
                        viewModelScope.launch(Dispatchers.IO) {
                            for (i in 1..10) {
                                delay(500L)
                                val pid = gamingModeEngine.resolveProcessPid(packageName)
                                if (pid > 0) {
                                    gamingModeEngine.promoteGamePid(packageName, pid)
                                    break
                                }
                            }
                        }
                    } else if (sessionPath == GamingPlatformPath.GENERIC) {
                        viewModelScope.launch(Dispatchers.IO) {
                            gamingModeEngine.promoteGamePid(packageName, 0)
                        }
                    }
                }
            }

            onLaunched?.invoke(freedMb)
        }
    }

    suspend fun manualBoostRam(whitelist: Set<String>): Pair<Long, Int> =
        PerformanceUtils.manualBoostRam(whitelist, deviceDiagnosticManager, shizukuManager, gamingModeEngine)

    fun refreshVivoPerfGameList() {
        if (!vivoSuiteGate.isVivoSuiteEnabled) return
        viewModelScope.launch {
            val raw = vivoGamingOptimizer.getRawPerfGameList()
            _rawPerfGameList.value = raw
            _vivoPerfGameList.value = raw.split(":").map { it.trim() }.filter { it.isNotBlank() }
        }
    }

    fun addAllLauncherGamesToPerfList(packages: Set<String>, onComplete: (Boolean) -> Unit) {
        if (packages.isEmpty()) {
            _effectChannel.trySend(PerformanceUiEffect.ShowToast("Kindly add a minimum of one game in game launcher"))
            onComplete(false)
            return
        }
        viewModelScope.launch {
            var allSuccess = true
            try {
                for (pkg in packages) {
                    val ok = vivoGamingOptimizer.injectPerfGameList(pkg)
                    if (!ok) allSuccess = false
                }
                val raw = vivoGamingOptimizer.getRawPerfGameList()
                _rawPerfGameList.value = raw
                _vivoPerfGameList.value = raw.split(":").map { it.trim() }.filter { it.isNotBlank() }
                val msg = if (allSuccess) "Added ${packages.size} game(s) to Perf List ✓" else "Some games could not be added to Perf List"
                _effectChannel.send(PerformanceUiEffect.ShowToast(msg))
            } catch (t: Throwable) {
                FrameXLog.e("addAllLauncherGamesToPerfList failed", t)
                allSuccess = false
            } finally {
                onComplete(allSuccess)
            }
        }
    }

    fun removeAllLauncherGamesFromPerfList(packages: Set<String>, onComplete: (Boolean) -> Unit) {
        if (packages.isEmpty()) {
            _effectChannel.trySend(PerformanceUiEffect.ShowToast("Kindly add a minimum of one game in game launcher"))
            onComplete(false)
            return
        }
        viewModelScope.launch {
            var allSuccess = true
            try {
                for (pkg in packages) {
                    val ok = vivoGamingOptimizer.removePerfGame(pkg)
                    if (!ok) allSuccess = false
                }
                val raw = vivoGamingOptimizer.getRawPerfGameList()
                _rawPerfGameList.value = raw
                _vivoPerfGameList.value = raw.split(":").map { it.trim() }.filter { it.isNotBlank() }
                val msg = if (allSuccess) "Removed ${packages.size} game(s) from Perf List ✓" else "Some games could not be removed from Perf List"
                _effectChannel.send(PerformanceUiEffect.ShowToast(msg))
            } catch (t: Throwable) {
                FrameXLog.e("removeAllLauncherGamesFromPerfList failed", t)
                allSuccess = false
            } finally {
                onComplete(allSuccess)
            }
        }
    }

    fun compileAllLauncherGamesSpeed(packages: Set<String>, onComplete: (Boolean) -> Unit) {
        if (packages.isEmpty()) {
            _effectChannel.trySend(PerformanceUiEffect.ShowToast("Kindly add a minimum of one game in game launcher"))
            onComplete(false)
            return
        }
        viewModelScope.launch {
            var allSuccess = true
            try {
                for (pkg in packages) {
                    val ok = vivoGamingOptimizer.compileSpeedAot(pkg)
                    if (!ok) allSuccess = false
                }
                val msg = if (allSuccess) "AOT compiled ${packages.size} app(s) to speed filter ✓" else "AOT compilation failed for one or more apps"
                _effectChannel.send(PerformanceUiEffect.ShowToast(msg))
            } catch (t: Throwable) {
                FrameXLog.e("compileAllLauncherGamesSpeed failed", t)
                allSuccess = false
            } finally {
                onComplete(allSuccess)
            }
        }
    }

    fun setAuditLoggingEnabled(enabled: Boolean) {
        settingsRepository.setAuditLoggingEnabled(enabled)
        if (!enabled) {
            vivoGamingOptimizer.clearAuditLogs()
        }
    }

    fun clearVivoAuditLogs() {
        vivoGamingOptimizer.clearAuditLogs()
    }

    fun getGameConfigBoostRam(pkg: String): Boolean = settingsRepository.getGameConfigBoostRam(pkg)
    fun setGameConfigBoostRam(pkg: String, enabled: Boolean) = settingsRepository.setGameConfigBoostRam(pkg, enabled)
    fun getGameConfigMemc(pkg: String): Boolean = settingsRepository.getGameConfigMemc(pkg)

    fun toggleMemc(packageName: String, enabled: Boolean, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = vivoGamingOptimizer.setMemcTargetFps(packageName, enabled)
            if (result) {
                settingsRepository.setGameConfigMemc(packageName, enabled)
            }
            onComplete(result)
        }
    }
}
