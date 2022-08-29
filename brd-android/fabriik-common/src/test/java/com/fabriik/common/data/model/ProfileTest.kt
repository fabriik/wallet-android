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

    private val profileNull: Profile? = null

    @Test
    fun isRegistrationNeeded_profileIsNull_returnTrue() {
        val actual = profileNull.isRegistrationNeeded()
        Assert.assertTrue(actual)
    }

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
    fun isRegistrationNeeded_rolesPropertyContainsOnlyUnverifiedRole_returnTrue() {
        isRegistrationNeeded_checkResult(listOf(ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun isEmailVerificationNeeded_profileIsNull_returnFalse() {
        val actual = profileNull.isEmailVerificationNeeded()
        Assert.assertFalse(actual)
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
    fun isEmailVerificationNeeded_rolesPropertyContainsOnlyUnverifiedRole_returnFalse() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerAndKyc1Roles_returnFalse() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), false
        )
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerAndKyc2Roles_returnFalse() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2), false
        )
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerAndUnverifiedRoles_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerKycLevel1AndUnverifiedRoles_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), true
        )
    }

    @Test
    fun isEmailVerificationNeeded_rolesPropertyContainsCustomerKycLevel2AndUnverifiedRoles_returnTrue() {
        isEmailVerificationNeeded_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2, ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun canUseBuy_profileIsNull_returnFalse() {
        val actual = profileNull.canUseBuy()
        Assert.assertFalse(actual)
    }

    @Test
    fun canUseTrade_profileIsNull_returnFalse() {
        val actual = profileNull.canUseTrade()
        Assert.assertFalse(actual)
    }

    @Test
    fun canUseTrade_rolesPropertyIsNull_returnFalse() {
        canUseTrade_checkResult(null, false)
    }

    @Test
    fun canUseTrade_rolesPropertyIsEmpty_returnFalse() {
        canUseTrade_checkResult(emptyList(), false)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsOnlyUnverifiedRole_returnFalse() {
        canUseTrade_checkResult(listOf(ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsOnlyCustomerRole_returnFalse() {
        canUseTrade_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsCustomerAndUnverifiedRoles_returnFalse() {
        canUseTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsCustomerKyc1AndUnverifiedRoles_returnFalse() {
        canUseTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsCustomerKyc2AndUnverifiedRoles_returnFalse() {
        canUseTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsCustomerAndKyc1Roles_returnTrue() {
        canUseTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), true)
    }

    @Test
    fun canUseTrade_rolesPropertyContainsCustomerAndKyc2Roles_returnTrue() {
        canUseTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2), true)
    }

    @Test
    fun canUseBuy_rolesPropertyIsNull_returnFalse() {
        canUseBuy_checkResult(null, false)
    }

    @Test
    fun canUseBuy_rolesPropertyIsEmpty_returnFalse() {
        canUseBuy_checkResult(emptyList(), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsOnlyUnverifiedRole_returnFalse() {
        canUseBuy_checkResult(listOf(ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsOnlyCustomerRole_returnFalse() {
        canUseBuy_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsCustomerAndUnverifiedRoles_returnFalse() {
        canUseBuy_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsCustomerKyc1AndUnverifiedRoles_returnFalse() {
        canUseBuy_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsCustomerKyc2AndUnverifiedRoles_returnFalse() {
        canUseBuy_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsCustomerAndKyc1Roles_returnFalse() {
        canUseBuy_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), false)
    }

    @Test
    fun canUseBuy_rolesPropertyContainsCustomerAndKyc2Roles_returnTrue() {
        canUseBuy_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2), true)
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

    private fun canUseTrade_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.canUseTrade()
        Assert.assertEquals(expectedResult, actual)
    }

    private fun canUseBuy_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.canUseBuy()
        Assert.assertEquals(expectedResult, actual)
    }
}