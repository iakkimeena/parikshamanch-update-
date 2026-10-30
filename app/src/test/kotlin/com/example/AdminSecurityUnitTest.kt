package com.example

import com.example.data.FirebaseAuthService
import com.example.model.AppMode
import com.example.model.AuthRole
import com.example.ui.AdminViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AdminSecurityUnitTest {

    private lateinit var viewModel: AdminViewModel

    @Before
    fun setUp() {
        viewModel = AdminViewModel()
    }

    @Test
    fun initialAppModeIsAuth() {
        assertEquals(AppMode.AUTH, viewModel.appMode.value)
    }

    @Test
    fun openAdminLoginTransitionsToAdminAuth() {
        viewModel.openAdminLogin()
        assertEquals(AppMode.ADMIN_AUTH, viewModel.appMode.value)
    }

    @Test
    fun unauthorizedEmailCannotDirectlySwitchToAdmin() {
        viewModel.login("student@gmail.com", "Test Student")
        assertEquals(AppMode.STUDENT, viewModel.appMode.value)
        assertEquals(AuthRole.STUDENT, viewModel.currentUserRole.value)

        // Attempting to switch to admin without admin email redirects to admin login
        viewModel.switchAppMode(AppMode.ADMIN)
        assertEquals(AppMode.ADMIN_AUTH, viewModel.appMode.value)
    }

    @Test
    fun primaryAdminEmailRoleDetermination() {
        val adminRole = FirebaseAuthService.determineRole(AdminViewModel.PRIMARY_ADMIN_EMAIL)
        assertEquals(AuthRole.ADMIN, adminRole)

        val studentRole = FirebaseAuthService.determineRole("other@gmail.com")
        assertEquals(AuthRole.STUDENT, studentRole)
    }

    @Test
    fun returnToStudentPortalResetsMode() {
        viewModel.openAdminLogin()
        assertEquals(AppMode.ADMIN_AUTH, viewModel.appMode.value)

        viewModel.returnToStudentPortal()
        assertEquals(AppMode.AUTH, viewModel.appMode.value)
    }
}
