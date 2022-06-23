package com.fabriik.common.utils

import com.fabriik.common.R
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(ParameterizedRobolectricTestRunner::class)
class FlagUtilTest(private val countryCode: String, private val drawableId: Int) {

    private val context = RuntimeEnvironment.application.applicationContext

    @Test
    fun getDrawableId_checkIfFlagExist() {
        val actual = FlagUtil.getDrawableId(context, countryCode)
        Assert.assertEquals(drawableId, actual)
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "countryCode: {0} drawableId: {1}")
        fun getParams() = listOf(
            arrayOf("ad", R.drawable.ic_flag_ad),
            arrayOf("ae", R.drawable.ic_flag_ae),
            arrayOf("af", R.drawable.ic_flag_af),
            arrayOf("ag", R.drawable.ic_flag_ag),
            arrayOf("ai", R.drawable.ic_flag_ai),
            arrayOf("al", R.drawable.ic_flag_al),
            arrayOf("am", R.drawable.ic_flag_am),
            arrayOf("an", R.drawable.ic_flag_an),
            arrayOf("ao", R.drawable.ic_flag_ao),
            arrayOf("ar", R.drawable.ic_flag_ar),
            arrayOf("as", R.drawable.ic_flag_as),
            arrayOf("at", R.drawable.ic_flag_at),
            arrayOf("au", R.drawable.ic_flag_au),
            arrayOf("aw", R.drawable.ic_flag_aw),
            arrayOf("ax", R.drawable.ic_flag_ax),
            arrayOf("az", R.drawable.ic_flag_az),
            arrayOf("ba", R.drawable.ic_flag_ba),
            arrayOf("bb", R.drawable.ic_flag_bb),
            arrayOf("bd", R.drawable.ic_flag_bd),
            arrayOf("be", R.drawable.ic_flag_be),
            arrayOf("bf", R.drawable.ic_flag_bf),
            arrayOf("bg", R.drawable.ic_flag_bg),
            arrayOf("bh", R.drawable.ic_flag_bh),
            arrayOf("bi", R.drawable.ic_flag_bi),
            arrayOf("bj", R.drawable.ic_flag_bj),
            arrayOf("bl", R.drawable.ic_flag_bl),
            arrayOf("bm", R.drawable.ic_flag_bm),
            arrayOf("bn", R.drawable.ic_flag_bn),
            arrayOf("bo", R.drawable.ic_flag_bo),
            arrayOf("br", R.drawable.ic_flag_br),
            arrayOf("bs", R.drawable.ic_flag_bs),
            arrayOf("bt", R.drawable.ic_flag_bt),
            arrayOf("bv", R.drawable.ic_flag_bv),
            arrayOf("bw", R.drawable.ic_flag_bw),
            arrayOf("by", R.drawable.ic_flag_by),
            arrayOf("bz", R.drawable.ic_flag_bz),
            arrayOf("ca", R.drawable.ic_flag_ca),
            arrayOf("cc", R.drawable.ic_flag_cc),
            arrayOf("cd", R.drawable.ic_flag_cd),
            arrayOf("cf", R.drawable.ic_flag_cf),
            arrayOf("cg", R.drawable.ic_flag_cg),
            arrayOf("ch", R.drawable.ic_flag_ch),
            arrayOf("ci", R.drawable.ic_flag_ci),
            arrayOf("ck", R.drawable.ic_flag_ck),
            arrayOf("cl", R.drawable.ic_flag_cl),
            arrayOf("cm", R.drawable.ic_flag_cm),
            arrayOf("cn", R.drawable.ic_flag_cn),
            arrayOf("co", R.drawable.ic_flag_co),
            arrayOf("cr", R.drawable.ic_flag_cr),
            arrayOf("cu", R.drawable.ic_flag_cu),
            arrayOf("cv", R.drawable.ic_flag_cv),
            arrayOf("cw", R.drawable.ic_flag_cw),
            arrayOf("cx", R.drawable.ic_flag_cx),
            arrayOf("cy", R.drawable.ic_flag_cy),
            arrayOf("cz", R.drawable.ic_flag_cz),
            arrayOf("de", R.drawable.ic_flag_de),
            arrayOf("dj", R.drawable.ic_flag_dj),
            arrayOf("dk", R.drawable.ic_flag_dk),
            arrayOf("dm", R.drawable.ic_flag_dm),
            arrayOf("do", R.drawable.ic_flag_do),
            arrayOf("dz", R.drawable.ic_flag_dz),
            arrayOf("ec", R.drawable.ic_flag_ec),
            arrayOf("ee", R.drawable.ic_flag_ee),
            arrayOf("eg", R.drawable.ic_flag_eg),
            arrayOf("er", R.drawable.ic_flag_er),
            arrayOf("es", R.drawable.ic_flag_es),
            arrayOf("et", R.drawable.ic_flag_et),
            arrayOf("fi", R.drawable.ic_flag_fi),
            arrayOf("fj", R.drawable.ic_flag_fj),
            arrayOf("fk", R.drawable.ic_flag_fk),
            arrayOf("fm", R.drawable.ic_flag_fm),
            arrayOf("fo", R.drawable.ic_flag_fo),
            arrayOf("fr", R.drawable.ic_flag_fr),
            arrayOf("ga", R.drawable.ic_flag_ga),
            arrayOf("gb", R.drawable.ic_flag_gb),
            arrayOf("gd", R.drawable.ic_flag_gd),
            arrayOf("ge", R.drawable.ic_flag_ge),
            arrayOf("gf", R.drawable.ic_flag_gf),
            arrayOf("gg", R.drawable.ic_flag_gg),
            arrayOf("gh", R.drawable.ic_flag_gh),
            arrayOf("gi", R.drawable.ic_flag_gi),
            arrayOf("gl", R.drawable.ic_flag_gl),
            arrayOf("gm", R.drawable.ic_flag_gm),
            arrayOf("gn", R.drawable.ic_flag_gn),
            arrayOf("gp", R.drawable.ic_flag_gp),
            arrayOf("gq", R.drawable.ic_flag_gq),
            arrayOf("gr", R.drawable.ic_flag_gr),
            arrayOf("gt", R.drawable.ic_flag_gt),
            arrayOf("gu", R.drawable.ic_flag_gu),
            arrayOf("gw", R.drawable.ic_flag_gw),
            arrayOf("gy", R.drawable.ic_flag_gy),
            arrayOf("hk", R.drawable.ic_flag_hk),
            arrayOf("hn", R.drawable.ic_flag_hn),
            arrayOf("hr", R.drawable.ic_flag_hr),
            arrayOf("ht", R.drawable.ic_flag_ht),
            arrayOf("hu", R.drawable.ic_flag_hu),
            arrayOf("id", R.drawable.ic_flag_id),
            arrayOf("ie", R.drawable.ic_flag_ie),
            arrayOf("il", R.drawable.ic_flag_il),
            arrayOf("im", R.drawable.ic_flag_im),
            arrayOf("in", R.drawable.ic_flag_in),
            arrayOf("iq", R.drawable.ic_flag_iq),
            arrayOf("ir", R.drawable.ic_flag_ir),
            arrayOf("is", R.drawable.ic_flag_is),
            arrayOf("it", R.drawable.ic_flag_it),
            arrayOf("je", R.drawable.ic_flag_je),
            arrayOf("jm", R.drawable.ic_flag_jm),
            arrayOf("jo", R.drawable.ic_flag_jo),
            arrayOf("jp", R.drawable.ic_flag_jp),
            arrayOf("ke", R.drawable.ic_flag_ke),
            arrayOf("kg", R.drawable.ic_flag_kg),
            arrayOf("kh", R.drawable.ic_flag_kh),
            arrayOf("ki", R.drawable.ic_flag_ki),
            arrayOf("km", R.drawable.ic_flag_km),
            arrayOf("kn", R.drawable.ic_flag_kn),
            arrayOf("kp", R.drawable.ic_flag_kp),
            arrayOf("kr", R.drawable.ic_flag_kr),
            arrayOf("kw", R.drawable.ic_flag_kw),
            arrayOf("ky", R.drawable.ic_flag_ky),
            arrayOf("kz", R.drawable.ic_flag_kz),
            arrayOf("la", R.drawable.ic_flag_la),
            arrayOf("lb", R.drawable.ic_flag_lb),
            arrayOf("lc", R.drawable.ic_flag_lc),
            arrayOf("li", R.drawable.ic_flag_li),
            arrayOf("lk", R.drawable.ic_flag_lk),
            arrayOf("lr", R.drawable.ic_flag_lr),
            arrayOf("ls", R.drawable.ic_flag_ls),
            arrayOf("lt", R.drawable.ic_flag_lt),
            arrayOf("lu", R.drawable.ic_flag_lu),
            arrayOf("lv", R.drawable.ic_flag_lv),
            arrayOf("ly", R.drawable.ic_flag_ly),
            arrayOf("ma", R.drawable.ic_flag_ma),
            arrayOf("mc", R.drawable.ic_flag_mc),
            arrayOf("md", R.drawable.ic_flag_md),
            arrayOf("me", R.drawable.ic_flag_me),
            arrayOf("mf", R.drawable.ic_flag_mf),
            arrayOf("mg", R.drawable.ic_flag_mg),
            arrayOf("mh", R.drawable.ic_flag_mh),
            arrayOf("mk", R.drawable.ic_flag_mk),
            arrayOf("ml", R.drawable.ic_flag_ml),
            arrayOf("mm", R.drawable.ic_flag_mm),
            arrayOf("mn", R.drawable.ic_flag_mn),
            arrayOf("mp", R.drawable.ic_flag_mp),
            arrayOf("mq", R.drawable.ic_flag_mq),
            arrayOf("mr", R.drawable.ic_flag_mr),
            arrayOf("ms", R.drawable.ic_flag_ms),
            arrayOf("mt", R.drawable.ic_flag_mt),
            arrayOf("mu", R.drawable.ic_flag_mu),
            arrayOf("mv", R.drawable.ic_flag_mv),
            arrayOf("mw", R.drawable.ic_flag_mw),
            arrayOf("mx", R.drawable.ic_flag_mx),
            arrayOf("my", R.drawable.ic_flag_my),
            arrayOf("mz", R.drawable.ic_flag_mz),
            arrayOf("na", R.drawable.ic_flag_na),
            arrayOf("nc", R.drawable.ic_flag_nc),
            arrayOf("ne", R.drawable.ic_flag_ne),
            arrayOf("nf", R.drawable.ic_flag_nf),
            arrayOf("ng", R.drawable.ic_flag_ng),
            arrayOf("ni", R.drawable.ic_flag_ni),
            arrayOf("nl", R.drawable.ic_flag_nl),
            arrayOf("no", R.drawable.ic_flag_no),
            arrayOf("np", R.drawable.ic_flag_np),
            arrayOf("nr", R.drawable.ic_flag_nr),
            arrayOf("nz", R.drawable.ic_flag_nz),
            arrayOf("om", R.drawable.ic_flag_om),
            arrayOf("pa", R.drawable.ic_flag_pa),
            arrayOf("pe", R.drawable.ic_flag_pe),
            arrayOf("pf", R.drawable.ic_flag_pf),
            arrayOf("pg", R.drawable.ic_flag_pg),
            arrayOf("ph", R.drawable.ic_flag_ph),
            arrayOf("pk", R.drawable.ic_flag_pk),
            arrayOf("pl", R.drawable.ic_flag_pl),
            arrayOf("pm", R.drawable.ic_flag_pm),
            arrayOf("pr", R.drawable.ic_flag_pr),
            arrayOf("ps", R.drawable.ic_flag_ps),
            arrayOf("pt", R.drawable.ic_flag_pt),
            arrayOf("pw", R.drawable.ic_flag_pw),
            arrayOf("py", R.drawable.ic_flag_py),
            arrayOf("qa", R.drawable.ic_flag_qa),
            arrayOf("ro", R.drawable.ic_flag_ro),
            arrayOf("rs", R.drawable.ic_flag_rs),
            arrayOf("ru", R.drawable.ic_flag_ru),
            arrayOf("rw", R.drawable.ic_flag_rw),
            arrayOf("sa", R.drawable.ic_flag_sa),
            arrayOf("sb", R.drawable.ic_flag_sb),
            arrayOf("sc", R.drawable.ic_flag_sc),
            arrayOf("sd", R.drawable.ic_flag_sd),
            arrayOf("se", R.drawable.ic_flag_se),
            arrayOf("sg", R.drawable.ic_flag_sg),
            arrayOf("sh", R.drawable.ic_flag_sh),
            arrayOf("si", R.drawable.ic_flag_si),
            arrayOf("sk", R.drawable.ic_flag_sk),
            arrayOf("sl", R.drawable.ic_flag_sl),
            arrayOf("sm", R.drawable.ic_flag_sm),
            arrayOf("sn", R.drawable.ic_flag_sn),
            arrayOf("so", R.drawable.ic_flag_so),
            arrayOf("sr", R.drawable.ic_flag_sr),
            arrayOf("ss", R.drawable.ic_flag_ss),
            arrayOf("st", R.drawable.ic_flag_st),
            arrayOf("sv", R.drawable.ic_flag_sv),
            arrayOf("sx", R.drawable.ic_flag_sx),
            arrayOf("sy", R.drawable.ic_flag_sy),
            arrayOf("sz", R.drawable.ic_flag_sz),
            arrayOf("tc", R.drawable.ic_flag_tc),
            arrayOf("td", R.drawable.ic_flag_td),
            arrayOf("tg", R.drawable.ic_flag_tg),
            arrayOf("th", R.drawable.ic_flag_th),
            arrayOf("tj", R.drawable.ic_flag_tj),
            arrayOf("tl", R.drawable.ic_flag_tl),
            arrayOf("tm", R.drawable.ic_flag_tm),
            arrayOf("tn", R.drawable.ic_flag_tn),
            arrayOf("to", R.drawable.ic_flag_to),
            arrayOf("tr", R.drawable.ic_flag_tr),
            arrayOf("tt", R.drawable.ic_flag_tt),
            arrayOf("tv", R.drawable.ic_flag_tv),
            arrayOf("tz", R.drawable.ic_flag_tz),
            arrayOf("ua", R.drawable.ic_flag_ua),
            arrayOf("ug", R.drawable.ic_flag_ug),
            arrayOf("us", R.drawable.ic_flag_us),
            arrayOf("uy", R.drawable.ic_flag_uy),
            arrayOf("uz", R.drawable.ic_flag_uz),
            arrayOf("va", R.drawable.ic_flag_va),
            arrayOf("vc", R.drawable.ic_flag_vc),
            arrayOf("ve", R.drawable.ic_flag_ve),
            arrayOf("vg", R.drawable.ic_flag_vg),
            arrayOf("vi", R.drawable.ic_flag_vi),
            arrayOf("vn", R.drawable.ic_flag_vn),
            arrayOf("vu", R.drawable.ic_flag_vu),
            arrayOf("wf", R.drawable.ic_flag_wf),
            arrayOf("ws", R.drawable.ic_flag_ws),
            arrayOf("xk", R.drawable.ic_flag_xk),
            arrayOf("ye", R.drawable.ic_flag_ye),
            arrayOf("yt", R.drawable.ic_flag_yt),
            arrayOf("za", R.drawable.ic_flag_za),
            arrayOf("zm", R.drawable.ic_flag_zm),
            arrayOf("zw", R.drawable.ic_flag_zw),
            arrayOf("XYZ", R.drawable.ic_flag_default) //unknown country code
        )
    }
}