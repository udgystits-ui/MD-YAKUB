package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.Build
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuraAccessibilityService : AccessibilityService() {

    companion object {
        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive

        private val _currentAppPackage = MutableStateFlow("")
        val currentAppPackage: StateFlow<String> = _currentAppPackage

        private var instance: AuraAccessibilityService? = null

        fun getInstance(): AuraAccessibilityService? = instance

        fun performGlobalAction(action: Int): Boolean {
            return instance?.performGlobalAction(action) ?: false
        }

        fun clickText(targetText: String): Boolean {
            val service = instance ?: return false
            val root = service.rootInActiveWindow ?: return false
            val nodes = root.findAccessibilityNodeInfosByText(targetText)
            for (node in nodes) {
                if (node.isClickable) {
                    val clicked = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    node.recycle()
                    root.recycle()
                    return clicked
                }
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable) {
                        val clicked = parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        parent.recycle()
                        node.recycle()
                        root.recycle()
                        return clicked
                    }
                    val nextParent = parent.parent
                    parent.recycle()
                    parent = nextParent
                }
                node.recycle()
            }
            root.recycle()
            return false
        }

        fun scrollScreen(forward: Boolean): Boolean {
            val service = instance ?: return false
            val root = service.rootInActiveWindow ?: return false
            val action = if (forward) AccessibilityNodeInfo.ACTION_SCROLL_FORWARD else AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD
            val success = root.performAction(action)
            root.recycle()
            return success
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceActive.value = true

        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val pkg = event.packageName?.toString() ?: ""
        if (pkg.isNotEmpty() && !pkg.contains("accessibility") && !pkg.contains("launcher")) {
            _currentAppPackage.value = pkg
        }
    }

    override fun onInterrupt() {
        _isServiceActive.value = false
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _isServiceActive.value = false
    }
}
