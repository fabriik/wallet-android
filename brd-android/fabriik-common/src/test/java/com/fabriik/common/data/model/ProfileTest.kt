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
    fun isUserRegistered_rolesIsNull_returnFalse() {
        isUserRegistered_checkResult(null, false)
    }

    @Test
    fun isUserRegistered_rolesIsEmpty_returnFalse() {
        isUserRegistered_checkResult(emptyList(), false)
    }

    @Test
    fun isUserRegistered_rolesDoesNotContainCustomerRole_returnFalse() {
        isUserRegistered_checkResult(listOf(ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun isUserRegistered_rolesContainsCustomerRole_returnTrue() {
        isUserRegistered_checkResult(listOf(ProfileRole.CUSTOMER), true)
    }

    @Test
    fun canUseBuyTrade_rolesIsNull_returnFalse() {
        canUseBuyTrade_checkResult(null, false)
    }

    @Test
    fun canUseBuyTrade_rolesIsEmpty_returnFalse() {
        canUseBuyTrade_checkResult(emptyList(), false)
    }

    @Test
    fun canUseBuyTrade_rolesContainsOnlyCustomerRole_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun canUseBuyTrade_rolesContainsKyc1AndUnverifiedRoles_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuyTrade_rolesContainsKyc2AndUnverifiedRoles_returnFalse() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1, ProfileRole.UNVERIFIED), false)
    }

    @Test
    fun canUseBuyTrade_rolesContainsKyc1Role_returnTrue() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), true)
    }

    @Test
    fun canUseBuyTrade_rolesContainsKyc2Role_returnTrue() {
        canUseBuyTrade_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), true)
    }

    @Test
    fun isUserVerificationRequired_rolesIsNull_returnFalse() {
        isUserVerificationRequired_checkResult(null, false)
    }

    @Test
    fun isUserVerificationRequired_rolesIsEmpty_returnFalse() {
        isUserVerificationRequired_checkResult(emptyList(), false)
    }

    @Test
    fun isUserVerificationRequired_rolesContainsCustomerRole_returnFalse() {
        isUserVerificationRequired_checkResult(listOf(ProfileRole.CUSTOMER), false)
    }

    @Test
    fun isUserVerificationRequired_rolesContainsCustomerAndKyc1Roles_returnFalse() {
        isUserVerificationRequired_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_1), false)
    }

    @Test
    fun isUserVerificationRequired_rolesContainsCustomerAndKyc2Roles_returnFalse() {
        isUserVerificationRequired_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.KYC_LEVEL_2), false)
    }

    @Test
    fun isUserVerificationRequired_rolesContainsUnverifiedRole_returnTrue() {
        isUserVerificationRequired_checkResult(listOf(ProfileRole.UNVERIFIED), true)
    }

    @Test
    fun isUserVerificationRequired_rolesContainsCustomerAndUnverifiedRoles_returnTrue() {
        isUserVerificationRequired_checkResult(listOf(ProfileRole.CUSTOMER, ProfileRole.UNVERIFIED), true)
    }

    private fun isUserRegistered_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.isUserRegistered()
        Assert.assertEquals(expectedResult, actual)
    }

    private fun canUseBuyTrade_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.canUseBuyTrade()
        Assert.assertEquals(expectedResult, actual)
    }

    private fun isUserVerificationRequired_checkResult(roles: List<ProfileRole>?, expectedResult: Boolean) {
        Mockito.`when`(profile.roles).thenReturn(roles)
        val actual = profile.isUserVerificationRequired()
        Assert.assertEquals(expectedResult, actual)
    }
}