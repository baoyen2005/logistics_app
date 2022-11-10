package com.example.baseapp.captures;

import android.content.res.Resources;
import android.text.TextUtils;

import com.example.baseapp.R;


public class MoneyReader implements IMoneyReader {
    private String[] sPo;
    private String[] sDonvi;
    private String[] sSo;
    //    private static MoneyReader moneyReader;
    private final String SPACE_KEY = " ";
    private final String EMPTY_KEY = "";
    private String ccy;
    Resources res;

    public MoneyReader(Resources res) {
        this.res = res;
        ccy = SPACE_KEY + res.getString(R.string.currency);
        sPo = new String[]{EMPTY_KEY, res.getString(R.string.thousand), res.getString(R.string.milion), res.getString(R.string.billion), res.getString(R.string.thousand_billion)};
        sSo = new String[]{"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};
        sDonvi = new String[]{EMPTY_KEY, "mươi", "trăm"};
    }

    @Override
    public String convert(String value) {
        return numberToString(value).replaceAll("[\\s]+[\\s]+", SPACE_KEY).trim() + getCcy();
    }

    private String numberToString(String number) {
        String sNumber = number.replaceAll("[^\\d]", EMPTY_KEY);
        String sReturn = EMPTY_KEY;
        int iLen = sNumber.length();
        String sNumber1 = EMPTY_KEY;
        for (int i = iLen - 1; i >= 0; i--) {
            sNumber1 += sNumber.charAt(i);
        }
        int iRe = 0;
        do {
            String sCut;
            if (iLen > 3) {
                sCut = sNumber1.substring((iRe * 3), (iRe * 3) + 3);
                sReturn = Read(sCut, iRe) + sReturn;
                iRe++;
                iLen -= 3;
            } else {
                sCut = sNumber1.substring((iRe * 3), (iRe * 3) + iLen);
                sReturn = Read(sCut, iRe) + sReturn;
                break;
            }
        } while (true);
        if (sReturn.length() > 1) {
            sReturn = sReturn.substring(0, 1).toUpperCase() + sReturn.substring(1);
        }
        return sReturn;
    }

    private String Read(String sNumber, int iPo) {
        String sReturn = EMPTY_KEY;
        if (!sNumber.equals("000")) {
            int iLen = sNumber.length();
            int iRe = 0;
            for (int i = 0; i < iLen; i++) {
                String minDV = SPACE_KEY + sDonvi[iRe] + SPACE_KEY;
                String sTemp = EMPTY_KEY + sNumber.charAt(i);
                int iTemp = Integer.parseInt(sTemp);
                String sRead = EMPTY_KEY;
                if (iTemp == 0) {
                    switch (iRe) {
                        case 0:
                            break;
                        case 1: {
                            if (Integer.parseInt(String.valueOf(sNumber.charAt(0))) != 0) {
                                sRead = "lẻ";
                            }
                            break;
                        }
                        case 2: {
                            sRead = "không trăm";
                            break;
                        }
                    }
                    sRead += SPACE_KEY;
                } else if (iTemp == 1) {
                    switch (iRe) {
                        case 1:
                            sRead = "mười" + SPACE_KEY;
                            break;
                        default:
                            sRead = "một" + minDV;
                            break;
                    }
                } else if (iTemp == 5) {
                    switch (iRe) {
                        case 0: {
                            if (sNumber.length() <= 1) {
                                sRead = "năm";
                            } else if (Integer.parseInt(EMPTY_KEY + sNumber.charAt(1)) != 0) {
                                sRead = "lăm";
                            } else
                                sRead = "năm";
                            break;
                        }
                        default:
                            sRead = sSo[iTemp] + SPACE_KEY + minDV;
                    }
                } else {
                    sRead = sSo[iTemp] + SPACE_KEY + minDV;
                }
                sReturn = sRead + SPACE_KEY + sReturn;
                iRe++;
            }
            if (!TextUtils.isEmpty(sReturn)) {
                String xpo = sPo[iPo];
                String key = (TextUtils.isEmpty(xpo) ? EMPTY_KEY : SPACE_KEY);
                sReturn += key + xpo + key;
            }
        }
        return sReturn;
    }

    public String getCcy() {

        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = SPACE_KEY + res.getString(R.string.currency);
    }
}
