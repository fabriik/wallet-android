package com.fabriik.common.data.model

import com.fabriik.common.data.enums.ProfileRole
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ProfileTest {

    @Mock
    lateinit var profile: Profile

    @Test
    fun isRegistrationNeeded_rolesPropertyIsNull_returnTrue() {
        isRegistrationNeeded_checkResult(null, true)
    }

    @Test
    fun isRegistrationNeeded_rolesPropertyIsEmpty_returnTrue() {
        isRegistrationNeeded_checkResult(emptyList(), true)
    }

    @Test
    fun isRegistrationNeeded_rolesPropertyDoesNotContainsCustomerOrUnverifiedRoles_returnTrue() {
        isRegistrationNeeded_checkResult(listOf(ProfileRole.KYC_LEVEL_1), true)
    }

    @Test
    fun isRegistrationNeeded_rolesPropertyContainsOnlyCustomerRole_returnFalse() {
        isRegistrationNeeded_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun isRegistrationNeeded_rolesPropertyContainsOnlyUnverifiedRole_returnFalse() {
        isRegistrationNeeded_checkResult(listOf(ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyIsNull_returnFalse() {
        isEmailVerificationNeeded_checkResult(null, false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyIsEmpty_returnFalse() {
        isEmailVerificationNeeded_checkResult(emptyList(), false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerRole_returnFalse() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerAndKyc1Roles_returnFalse() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerAndKyc2Roles_returnFalse() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2), false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsUnverifiedRole_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerAndUnverifiedRoles_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerKycLevel1AndUnverifiedRoles_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerKycLevel2AndUnverifiedRoles_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2, ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyIsNull_returnFalse() {
        canUseBuyTrade_checkResult(null, false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyIsEmpty_returnFalse() {
        canUseBuyTrade_checkResult(emptyList(), false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsOnlyUnverifiedRole_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsOnlyCustomerRole_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsCustomerAndUnverifiedRoles_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsCustomerKyc1AndUnverifiedRoles_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsCustomerKyc2AndUnverifiedRoles_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsCustomerAndKyc1Roles_returnTrue() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), true)
    }

    @Test
    fun canUseBuyTrade_rolesPropertyContainsCustomerAndKyc2Roles_returnTrue() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), true)
    }

    private fun isRegistrationNeeded_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.isRegistrationNeeded()
        Assert.assertEquals(expectedResult, actual)
    }

    private fun isEmailVerificationNeeded_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.isEmailVerificationNeeded()
        Assert.assertEquals(expectedResult, actual)
    }

    private fun canUseBuyTrade_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.canUseBuyTrade()
        Assert.assertEquals(expectedResult, actual)
    }
}