package com.switchone.application.exchange.dto.response;

public record ExchangeRateResponse(
        Integer result,
        String cur_unit,
        String ttb,
        String tts,
        String deal_bas_r,
        String bkpr,
        String yy_efee_r,
        String ten_dd_efee_r,
        String kftc_bkpr,
        String kftc_deal_bas_r,
        String cur_nm
) {
}
