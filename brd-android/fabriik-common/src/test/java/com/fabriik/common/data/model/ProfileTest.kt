package com.fabriik.common.data.model

import com.fabriik.common.data.enums.KycStatus
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
    fun isUserRegistered_statusIsNull_returnFalse() {
        isUserRegistered_checkResult(null, false)
    }

    @Test
    fun isUserRegistered_statusIsDefault_returnFalse() {
        isUserRegistered_checkResult(KycStatus.DEFAULT, false)
    }

    @Test
    fun isUserRegistered_statusIsEmailVerificationPending_returnFalse() {
        isUserRegistered_checkResult(KycStatus.EMAIL_VERIFICATION_PENDING, false)
    }

    @Test
    fun isUserRegistered_statusIsEmailVerified_returnTrue() {
        isUserRegistered_checkResult(KycStatus.EMAIL_VERIFIED, true)
    }

    @Test
    fun isUserRegistered_statusIsKyc1_returnTrue() {
        isUserRegistered_checkResult(KycStatus.KYC1, true)
    }

    @Test
    fun isUserRegistered_statusIsKyc2Expired_returnTrue() {
        isUserRegistered_checkResult(KycStatus.KYC2_EXPIRED, true)
    }

    @Test
    fun isUserRegistered_statusIsKyc2Declined_returnTrue() {
        isUserRegistered_checkResult(KycStatus.KYC2_DECLINED, true)
    }

    @Test
    fun isUserRegistered_statusIsKyc2Submitted_returnTrue() {
        isUserRegistered_checkResult(KycStatus.KYC2_SUBMITTED, true)
    }

    @Test
    fun isUserRegistered_statusIsKyc2ResubmissionRequested_returnTrue() {
        isUserRegistered_checkResult(KycStatus.KYC2_RESUBMISSION_REQUESTED, true)
    }

    @Test
    fun isUserRegistered_statusIsKyc2_returnTrue() {
        isUserRegistered_checkResult(KycStatus.KYC2, true)
    }

    @Test
    fun canUseBuyTrade_statusIsNull_returnFalse() {
        canUseBuyTrade_checkResult(null, false)
    }

    @Test
    fun canUseBuyTrade_statusIsDefault_returnFalse() {
        canUseBuyTrade_checkResult(KycStatus.DEFAULT, false)
    }

    @Test
    fun canUseBuyTrade_statusIsEmailVerificationPending_returnFalse() {
        canUseBuyTrade_checkResult(KycStatus.EMAIL_VERIFICATION_PENDING, false)
    }

    @Test
    fun canUseBuyTrade_statusIsEmailVerified_returnFalse() {
        canUseBuyTrade_checkResult(KycStatus.EMAIL_VERIFIED, false)
    }

    @Test
    fun canUseBuyTrade_statusIsKyc1_returnTrue() {
        canUseBuyTrade_checkResult(KycStatus.KYC1, true)
    }

    @Test
    fun canUseBuyTrade_statusIsKyc2Expired_returnTrue() {
        canUseBuyTrade_checkResult(KycStatus.KYC2_EXPIRED, true)
    }

    @Test
    fun canUseBuyTrade_statusIsKyc2Declined_returnTrue() {
        canUseBuyTrade_checkResult(KycStatus.KYC2_DECLINED, true)
    }

    @Test
    fun canUseBuyTrade_statusIsKyc2Submitted_returnTrue() {
        canUseBuyTrade_checkResult(KycStatus.KYC2_SUBMITTED, true)
    }

    @Test
    fun canUseBuyTrade_statusIsKyc2ResubmissionRequested_returnTrue() {
        canUseBuyTrade_checkResult(KycStatus.KYC2_RESUBMISSION_REQUESTED, true)
    }

    @Test
    fun canUseBuyTrade_statusIsKyc2_returnTrue() {
        canUseBuyTrade_checkResult(KycStatus.KYC2, true)
    }

    private fun isUserRegistered_checkResult(status: KycStatus?, expectedResult: Boolean) {
        Mockito.`when`(profile.kycStatus).thenReturn(status)
        val actual = profile.isUserRegistered()
        Assert.assertEquals(expectedResult, actual)
    }

    private fun canUseBuyTrade_checkResult(status: KycStatus?, expectedResult: Boolean) {
        Mockito.`when`(profile.kycStatus).thenReturn(status)
        val actual = profile.canUseBuyTrade()
        Assert.assertEquals(expectedResult, actual)
    }
}