package com.paypad.parator.utils;

import android.content.Intent;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannedString;
import android.widget.TextView;

import net.nightwhistler.htmlspanner.HtmlSpanner;

public class ReceiptHtmlContentHelper {

    public static String getReceiptHtmlContent(){
        String allText = "";
        String content1 = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Document</title>\n" +
                "</head>\n" +
                "\n" ;

        String content2 =
                "<body>\n" +
                "    <div\n" +
                "        style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:30px;color:#3d454d;font-weight:normal;width:100%!important;min-height:400px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0;background-color:#f2f4f5\">\n" +
                "        <div style=\"font-size:0;max-height:0;overflow:hidden;display:none\">\n" +
                "            Receipt for ₺35.50 at My Business on 08/09/2020, 12:24.</div>\n" +
                "        <div style=\"min-height:400px;background-color:#f2f4f5!important;width:100%;line-height:100%\">\n" +
                "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\"\n" +
                "                style=\"border-collapse:collapse;border-spacing:0;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                <tbody>\n" +
                "                    <tr>\n" +
                "                        <td align=\"center\" valign=\"top\"\n" +
                "                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                            <table align=\"center\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                style=\"border-collapse:collapse;border-spacing:0;text-align:left;margin-left:auto;margin-right:auto;margin-top:16px;width:100%;min-width:320px;max-width:375px;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                <tbody>\n" +
                "                                    <tr>\n" +
                "                                        <td align=\"center\"\n" +
                "                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" width=\"100%\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;overflow:hidden;width:100%;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <table bgcolor=\"#43505e\" cellpadding=\"0\" cellspacing=\"0\"\n";


        String content3 =
                "                                                                style=\"border-collapse:collapse;border-spacing:0;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                                <tbody>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td valign=\"bottom\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <img alt=\"gray\" width=\"375\" height=\"3\"\n" +
                "                                                                                style=\"border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0;display:block;max-width:375px;width:100%\"\n" +
                "                                                                                src=\"https://ci4.googleusercontent.com/proxy/c8UUVhMxc4pvj2kCE0-gmR6x6H28sAk7oZtunr4_ZsLJXQ6TxnsqDuvW0ShRsMDFZjFHfWyBcvswxOucn7-OsLDtJk5S5pvfwaEB2KKeANYCspf01_LQ3tl5r2LUB5S6VJx6rltsxj87vGayjKorQHq9ZY2qOSZ4WqiXAVs2ESSXI7BZTI1y6878N9EkOVqKXNliu4E9eG_CSbsNfDAyUCo-Zn7wyCw4VpOqwb7bKRfKZjmP_EWvOax88D8WE8FR-6NYwfvAW6JAuWBX-rPSVMQBdCygr0AC1Oe4JEl3evIUxHyWVIkHb7Q87DO9p0Yn4cymT-c02ZHvoypiEc7hBE51Tq1eHKDbmh8VNxVA0Fb61KuHpRmQ_2gcnJqReVuvDPsP1wTpSNgfSzL7S8PakwchU2PQ2OPNJijJilehIq4SyKMmafNIW5sgpBqNu6NvzfljhIa4CyzSRpb7cmx_RjdRSGKEWqZ5cVFyCZu-XLsnhbl6HYo0NR_VFx_Z6p7gF0ka0ggban6rpLDEonONtR-z50qdquNbjY59TdoZuSpE2kzDNrRnrqE=s0-d-e1-ft#https://d2isyty7gbnm74.cloudfront.net/LbW-IA0sgIvG_SC4Pf7zYgGaaBY=/fit-in/750x6/filters:fill(43505e):watermark(https://d3g64w74of3jgu.cloudfront.net/receipts/assets/receipt-top-edge-mask-e4b3ac1f0660315cfe627b6f6655ecb62d6c8cded4c43526d139b7959ccda317.png,0,0,0):quality(100)/https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                class=\"CToWUd\"></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr style=\"display:none\">\n" +
                "                                                                        <td valign=\"bottom\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <img alt=\"white\" width=\"375\" height=\"3\"\n" +
                "                                                                                style=\"border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0;display:block;max-width:375px;width:100%\"\n" +
                "                                                                                src=\"https://ci4.googleusercontent.com/proxy/093-ExQaQQV_B0Wx2Ama1j8CKddEapHaAVyKT-uhsaAx7LmcFVm_joPS4Nka-9Fd8kACKKc4n67JCE6Dle4LwXJziTvBLJta5x9TJSRmSyzzvxmPdjGV7l6xYKAcDN02celLtpvkHfZg5HgS2RzQb_gZY065ost0Nmkvj_b3CrRZz5LBqEYsn98xlWQ8aIkQusLDw4ghSEZSvjfpbvj-4CKj3VqMjJUZQ-oyCsgqnzziHT8FrWRUkxU_gYlQZo510lowxxSfHJQT5L3Ts3yAjuGlz4eMIrsGxFN_c_qSQc2M-COT2xyuh5vovTBYMItkU_3SXe488vjrUOTfOyGXesEPsrA862GIls3CfA3v-4te8tBMiYiULgKVF_XhgFdDEDttgZ16CFM5kFRtLEbhloQU9CKO_flnfwhIZBJQjCux8fJCg8c82Ch_HwtqUNUjR4S3C4negGB0te9AFiT0MKf-ZLfbK7JCIYhkn6081ZZ5ODp-TplIcDp6on6NpSK2YMjPot1WZ5J-iDw00vfBA_aqpvapxUDxwiBwiUpUZyWQJQliS61kCS4H=s0-d-e1-ft#https://d2isyty7gbnm74.cloudfront.net/xCrNbEygQaJYQ2wL0iyzfqdJJik=/fit-in/750x6/filters:fill(43505e):watermark(https://d3g64w74of3jgu.cloudfront.net/receipts/assets/receipt-top-edge-white-e04dd053ad1839f8e3e91459faf5e1606dca195cd4c6fbd74e195bafeabae1eb.png,0,0,0):quality(100)/https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                class=\"CToWUd\"></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"8\" colspan=\"1\"\n" +
                "                                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"center\" valign=\"middle\" width=\"100%\"\n" +
                "                                                                            height=\"141\"\n" +
                "                                                                            style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:30px;color:white;font-weight:normal;border-collapse:collapse;width:100%;height:141px;padding-top:0;padding-right:36px;padding-bottom:0;padding-left:36px\">\n" +
                "                                                                            <table width=\"100%\" cellpadding=\"0\"\n" +
                "                                                                                cellspacing=\"0\"\n" ;


        String content4 =
                "                                                                                style=\"border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%;word-wrap:break-word;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                                                <tbody>\n" +
                "                                                                                    <tr>\n" +
                "                                                                                        <td align=\"center\"\n" +
                "                                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                                            <img width=\"64\" height=\"64\"\n" +
                "                                                                                                border=\"0\" alt=\"Logo\"\n" +
                "                                                                                                style=\"border-radius:11px;vertical-align:middle;border-top-color:#ffffff;border-right-color:#ffffff;border-bottom-color:#ffffff;border-left-color:#ffffff;border-top-style:solid;border-right-style:solid;border-bottom-style:solid;border-left-style:solid;border-top-width:3px;border-right-width:3px;border-bottom-width:3px;border-left-width:3px\"\n" +
                "                                                                                                src=\"https://ci5.googleusercontent.com/proxy/ILUbmX0wlRLzpKk8OiYO7PNWG1A3X13r5izK6UShDFdtH7DVgmUEZT9emPFO7UqyRKLr3Lw8Eb8hP6WOPLZDCQASaBRFlm-t5L9WY3mYicvDjG4257s7_hZjOXmRERI31LSjEUHWaJW5eXJqM8ESNoY3SmB78esOqkHPwt5jjAPTVLQUMfeIIhEyGPjJn8Yszq1cfFtgoq9sJ9Gd7tOOTS6lzJTUXKSUrXSibMB0lPH3SPd7Jdx5aINMjrziVL8NjO9xwM_lOqXMhBQyJALGG1oEnmjXXMAGaZLUr6uyUOsERXRdMOcHwAIYLt-8I9nliE7VEjXXWB7GKO_jNZWHKq5j3MmvM7-dZm6qtwbG7ClgSnEHIoZZRwjQA40gJnAYPpfUBpz7JtD8ncVIcrzpUKxjFQ_a6ZqRomkWHOuj-0nqdg44w3v3aUlpoi-eJ-6pJM9cr7ZaHmWmUj4Xqe2jdY57fhCVadj9ElB6BLWYiwyz7jKN4s28ldjjC04Gt42zfLQ99Wyk4fSwBxL373S4fI7gcL49QPzvG_-lwfIGqCDyyl3aKmo=s0-d-e1-ft#https://d2isyty7gbnm74.cloudfront.net/EjICo-2OcZWVqLc5bfNXfo0AfNI=/128x128/filters:watermark(https://d3g64w74of3jgu.cloudfront.net/receipts/assets/default-merchant-image-7f1ee352828e1d2fbf1d917bc26bb4e82e1c47594c2195d35c5c607c859039e7.png,40,40,0):quality(100):format(png)/https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                                class=\"CToWUd\">\n" +
                "                                                                                        </td>\n" +
                "                                                                                    </tr>\n" +
                "                                                                                    <tr>\n" +
                "                                                                                        <td align=\"center\"\n" +
                "                                                                                            style=\"line-height:18px;font-size:16px;border-collapse:collapse;letter-spacing:0.3px;color:white;font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-weight:500;padding-top:24px;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                                            My Business</td>\n" +
                "                                                                                    </tr>\n" +
                "                                                                                </tbody>\n" +
                "                                                                            </table>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"center\" valign=\"bottom\" height=\"8\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;height:8px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"border-bottom-style:solid;border-bottom-color:#546476;height:0;width:0;border-right-style:solid;border-right-color:transparent;border-left-style:solid;border-left-color:transparent;vertical-align:bottom;border-top-width:0;border-right-width:8px;border-bottom-width:8px;border-left-width:8px\">\n" +
                "                                                                            </div>\n" +
                "                                                                        </td>\n" ;


        String content5 =
                "                                                                    </tr>\n" +
                "                                                                </tbody>\n" +
                "                                                            </table>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table bgcolor=\"#546476\" width=\"100%\" height=\"120\" cellpadding=\"0\"\n" +
                "                                                cellspacing=\"0\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;height:120px;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"12\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\"\n" +
                "                                                            style=\"line-height:16px;font-size:12px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <div\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:16px;font-weight:normal;letter-spacing:0.2px;line-height:24px;color:white\">\n" +
                "                                                                How was your experience?</div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"4\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" ;

        String content6 =
                "                                                        <td align=\"center\" width=\"100%\" height=\"100%\"\n" +
                "                                                            style=\"line-height:16px;font-size:12px;border-collapse:collapse;width:100%;height:100%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <table cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                                                style=\"border-collapse:collapse;border-spacing:0;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                                <tbody>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"center\" valign=\"middle\" width=\"74\"\n" +
                "                                                                            height=\"52\"\n" +
                "                                                                            style=\"line-height:16px;font-size:12px;border-collapse:collapse;width:74px;height:52px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <a id=\"m_7465221761897056103x_question-positive\"\n" +
                "                                                                                href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Faccount.squareup.com%2Fresponse%2Fr17B6Z7J1Y36Z5D%2F1/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/_jJn755XwvlMOD2seAYjlH9fRq8=178\"\n" +
                "                                                                                style=\"text-decoration:none;vertical-align:middle;display:block;width:100%;height:100%\"\n" +
                "                                                                                target=\"_blank\"\n" +
                "                                                                                data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Faccount.squareup.com%252Fresponse%252Fr17B6Z7J1Y36Z5D%252F1/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/_jJn755XwvlMOD2seAYjlH9fRq8%3D178&amp;source=gmail&amp;ust=1599644761294000&amp;usg=AFQjCNG-DX4sOfOCfNnOLwgnvzQApxwZ7w\">\n" +
                "                                                                                <div><img alt=\"Positive\"\n" +
                "                                                                                        title=\"Positive\" width=\"64\"\n" +
                "                                                                                        height=\"64\" border=\"0\"\n" +
                "                                                                                        style=\"vertical-align:middle;display:inline-block\"\n" +
                "                                                                                        src=\"https://ci5.googleusercontent.com/proxy/eQeGlY7uowGtAB3gyR03gkITyEGs3vbu2NmNlKhIjdKkGZqdanMGlZBzYdOMTZ4IWRvnazZyzbro1_TyIEzYkfjKI1-Dn8rpWIsPwxvsU9ZAtqpE3WI69iwwQBTQ2KOai8-KZKL3LzDIzQ7dX7FbacOyFCJXa9pc7xeAqn9ej1zNt-slw9eD3mOch04YHnQWqBaOd_DqoCQrzNESFO1gg8UrKfaIzAPyikHisxVGV9DZP7036ge_mL-Zy1Fny6sfxd65S9eJnEWeGvbKVDHhWcd-nCJtyyZe5FdB2FTkM9H5Tyy09hi5cREVCU6gp72seuDCY1c2_CBf2FdlD5FcyUzgoJZlE_R7xMd9gEJRJjHF=s0-d-e1-ft#https://d2isyty7gbnm74.cloudfront.net/uc-fQX9UfIOEN9tSFX9uHRCywd4=/128x128/filters:fill(546476,true):format(png)/https://d3g64w74of3jgu.cloudfront.net/receipts/assets/feedback-positive-ac20d76ea8053764cf8e1993d6ecc0429749ef06fffcfb0e55627b7a8b81d00c.png\"\n" +
                "                                                                                        class=\"CToWUd\"></div>\n" +
                "                                                                            </a></td>\n" +
                "                                                                        <td align=\"center\" valign=\"middle\" width=\"74\"\n" +
                "                                                                            height=\"52\"\n" +
                "                                                                            style=\"line-height:16px;font-size:12px;border-collapse:collapse;width:74px;height:52px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <a id=\"m_7465221761897056103x_question-negative\"\n" +
                "                                                                                href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Faccount.squareup.com%2Fresponse%2Fr17B6Z7J1Y36Z5D%2F-1/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/wjVRhbUHZMlGmY_Lw_tYg23Sswc=178\"\n" +
                "                                                                                style=\"text-decoration:none;vertical-align:middle;display:block;width:100%;height:100%\"\n" ;

        String content7 =
                "                                                                                target=\"_blank\"\n" +
                "                                                                                data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Faccount.squareup.com%252Fresponse%252Fr17B6Z7J1Y36Z5D%252F-1/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/wjVRhbUHZMlGmY_Lw_tYg23Sswc%3D178&amp;source=gmail&amp;ust=1599644761294000&amp;usg=AFQjCNH3dnQrLPSQgllH7q8wO3qFW54BgQ\">\n" +
                "                                                                                <div><img alt=\"Negative\"\n" +
                "                                                                                        title=\"Negative\" width=\"64\"\n" +
                "                                                                                        height=\"64\" border=\"0\"\n" +
                "                                                                                        style=\"vertical-align:middle;display:inline-block\"\n" +
                "                                                                                        src=\"https://ci6.googleusercontent.com/proxy/OUPBX3J-NVEC8-lMYLAdO7NNKeZRfdhp_nsk_vf2LMtOgHZlZXsShePWHkVILcgRmyS37Bs2sYFvhZwLo6LWR5UvvafeLxUmIkr2u_0zNVxOrMiOeWmO6pG72b08YcIICm4Vp857bxnESnB2gyRpfSvxmCvPfHXh-Y0un2ZTdbu8SFkhp4QJvuqUP7DUfoLk9IMn9rnq3HKOM1L-fUngLtnHyEQXoFk3Va54Y3dmKxUgz1WuBCIHr3rFit3mNg9ZtTzzUOI1xcry9AbiXmKp_e91AleQD0t6gdFcXpj1h71ypzmBIh2vu906HNjao6BA10_4AfRaMYBILPXoAT6tRHMXBq9eABi08bCkXwox0-yR=s0-d-e1-ft#https://d2isyty7gbnm74.cloudfront.net/Ds5_niJPx7nYXRPU7vnjxHyIpFw=/128x128/filters:fill(546476,true):format(png)/https://d3g64w74of3jgu.cloudfront.net/receipts/assets/feedback-negative-e37f79db3f0e129220b0544cd8ca9dcd0e772d4d6f6310b8efaee34990ea86ce.png\"\n" +
                "                                                                                        class=\"CToWUd\"></div>\n" +
                "                                                                            </a></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                </tbody>\n" +
                "                                                            </table>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"16\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table bgcolor=\"white\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" ;


        String content8 =
                "                                                        <td width=\"100%\" height=\"24\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;color:#3d454d;font-weight:normal;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\"\n" +
                "                                                            style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:30px;color:#3d454d;font-weight:normal;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <div align=\"center\"\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-weight:500;color:#3d454d;font-size:64px;line-height:64px;white-space:nowrap\">\n" +
                "                                                                <div style=\"white-space:nowrap\"><span\n" +
                "                                                                        style=\"font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-weight:500;font-size:26px;vertical-align:super;line-height:1\">₺</span>35.50\n" +
                "                                                                </div>\n" +
                "                                                            </div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"8\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;color:#3d454d;font-weight:normal;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"white\"\n" +
                "                                                width=\"100%\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td bgcolor=\"#ffffff\" width=\"16\"\n" ;


        String content9 =
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" + "                                                            <img width=\"16\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                        <td\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "                                                                bgcolor=\"white\" width=\"100%\"\n" +
                "                                                                style=\"border-collapse:separate;border-spacing:0;width:100%;min-width:100%;word-wrap:break-word;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                                <tbody>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"8\" colspan=\"1\"\n" +
                "                                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"3.5\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td colspan=\"3\" height=\"1\"\n" +
                "                                                                            style=\"border-top-width:1px;border-top-color:#e0e1e2;border-top-style:dashed;line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <img width=\"1\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                class=\"CToWUd\"></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" ;


        String content10 =
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" valign=\"top\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;max-width:305px;word-break:break-word;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Cay × 2</div>\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                (₺5.00 ea.)</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" valign=\"top\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺10.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" valign=\"top\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;max-width:305px;word-break:break-word;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" ;


        String content11 =
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Fanta</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" valign=\"top\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺10.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" valign=\"top\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;max-width:305px;word-break:break-word;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Kek</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" valign=\"top\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺20.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" valign=\"top\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;max-width:305px;word-break:break-word;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Kola</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" valign=\"top\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" ;


        String content12 =
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺10.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" valign=\"top\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;max-width:305px;word-break:break-word;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Custom Amount</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" valign=\"top\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺40.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td colspan=\"3\" height=\"1\"\n" +
                "                                                                            style=\"border-top-width:1px;border-top-color:#e0e1e2;border-top-style:dashed;line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <img width=\"1\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                class=\"CToWUd\"></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" ;

        String content13 =
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#00bd20;font-weight:500;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                % 10 </div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#00bd20;font-weight:500;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                -₺9.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#00bd20;font-weight:500;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                10tl </div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue-Medium,&quot;Helvetica Neue Medium&quot;,Helvetica-Bold,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#00bd20;font-weight:500;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                -₺10.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td colspan=\"3\" height=\"1\"\n" +
                "                                                                            style=\"border-top-width:1px;border-top-color:#e0e1e2;border-top-style:dashed;line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <img width=\"1\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                class=\"CToWUd\"></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" ;

        String content14 =
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Purchase Subtotal</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺71.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" colspan=\"2\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Sales Tax - included, ₺5.26</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" width=\"70%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Total</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:bold;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺71.00</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td colspan=\"3\" height=\"1\"\n" +
                "                                                                            style=\"border-top-width:1px;border-top-color:#e0e1e2;border-top-style:dashed;line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <img width=\"1\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                                class=\"CToWUd\"></td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"11\" colspan=\"3\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" width=\"70%\"\n" ;


        String content15 =
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:70%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Amount</div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" width=\"30%\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:30%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#3d454d;font-weight:normal;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                ₺35.50</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                </tbody>\n" +
                "                                                            </table>\n" +
                "                                                        </td>\n" +
                "                                                        <td bgcolor=\"#ffffff\" width=\"16\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img width=\"16\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"white\"\n" +
                "                                                width=\"100%\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td bgcolor=\"#ffffff\" width=\"16\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img width=\"16\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                        <td\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "                                                                bgcolor=\"white\" width=\"100%\"\n" +
                "                                                                style=\"border-collapse:separate;border-spacing:0;width:100%;min-width:100%;word-wrap:break-word;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                                <tbody>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"16\" colspan=\"2\"\n" +
                "                                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                </tbody>\n" +
                "                                                            </table>\n" +
                "                                                        </td>\n" +
                "                                                        <td bgcolor=\"#ffffff\" width=\"16\"\n" ;

        String content16 =
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img width=\"16\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table bgcolor=\"white\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;table-layout:fixed;word-wrap:break-word;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\" width=\"100%\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:100%;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img align=\"center\" border=\"0\" width=\"375\"\n" +
                "                                                                style=\"display:block;max-width:375px;width:100%;max-height:120px\"\n" +
                "                                                                src=\"https://ci4.googleusercontent.com/proxy/EO0pb-9YonGwzcn_vjompnB9TAU6xRoO6SvWEsafCBWOpjdR6us3rYTzzz2W4zEQcCcl81UAGN-pzICxD8okIbNCTdvDBFBgC7hYDwkSHRFaTvB9iGqzSL-hODgHRJ9f57qts__3qymlu9tvD0PdYZFXieNIlyFVnSXy8uouC5wRT0nF4LJzC43esIDVHYm4nS3sNp2sE7LUX9WnjGcVohEZYAb0gk3UplpGt4M7ZMoz3aWHUt4FTfwqOGQIGtzK9qYvQ17fmAQL-qByHBpl6HfnEJYQlvBO-QHsatxSrlKRkFRQCYxpcc9GdoYoRPxeJ8bJNThh1Ut055MV3D4hg_VOOCXbK-_KUGZsIs0vU5_8_m4_FP-52ig3CTEVFarO2ravl2tdKSKfwYzG-kGIpxUshjFN6V1iRhZCaHQsB8AJ4Dw8NUi0wEO67C_XEVoACrzQ8zikAOuMUkxlKFRxVal0rxsWAemJ3g4=s0-d-e1-ft#https://api.mapbox.com/styles/v1/square/ck93u4swt1ztg1ip8o9brpbk8/static/url-http%3A%2F%2Fs3.amazonaws.com%2Fsquare-receipts-production%2Freceipts%2Fstatic-assets%2Fmap-pin-140924-546476.png(-88.202,41.666)/-88.202,41.666,15/375x120@2x?access_token=pk.eyJ1Ijoic3F1YXJlIiwiYSI6IlBvOHNOSGcifQ.UEuIIhAN8OO4uCCqLShcJw&amp;attribution=false&amp;logo=false\"\n" +
                "                                                                class=\"CToWUd a6T\" tabindex=\"0\">\n" +
                "                                                            <div class=\"a6S\" dir=\"ltr\" style=\"opacity: 0.01;\">\n" +
                "                                                                <div id=\":90\" class=\"T-I J-J5-Ji aQv T-I-ax7 L3 a5q\"\n" +
                "                                                                    title=\"İndir\" role=\"button\" tabindex=\"0\"\n" +
                "                                                                    aria-label=\" adlı eki indir\"\n" +
                "                                                                    data-tooltip-class=\"a1V\">\n" +
                "                                                                    <div class=\"aSK J-J5-Ji aYr\"></div>\n" +
                "                                                                </div>\n" +
                "                                                            </div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td colspan=\"2\" height=\"1\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"white\"\n" +
                "                                                width=\"100%\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td bgcolor=\"#ffffff\" width=\"16\"\n" ;

        String content17 =
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img width=\"16\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                        <td\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"\n" +
                "                                                                bgcolor=\"white\" width=\"100%\"\n" +
                "                                                                style=\"border-collapse:collapse;border-spacing:0;width:100%;min-width:100%;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                                <tbody>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"center\" valign=\"top\" width=\"140\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:140px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td width=\"100%\" height=\"16\" colspan=\"1\"\n" +
                "                                                                            valign=\"top\"\n" +
                "                                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;width:140px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" valign=\"top\" width=\"140\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:140px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#85898c;font-weight:normal;vertical-align:top;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                Cash</div>\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#85898c;font-weight:normal;vertical-align:top;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                <img width=\"40\" height=\"16\" border=\"0\"\n" +
                "                                                                                    valign=\"bottom\" alt=\"\"\n" +
                "                                                                                    style=\"display:block;vertical-align:top\"\n" +
                "                                                                                    src=\"https://ci3.googleusercontent.com/proxy/OVVto2VKVzcHu8VMdEgV3ZF0jkd8kvszew3ahBDwiHsJbDTA5gyfIx_-a8cI9RIsZu1clbS3O1lG2Y7eCPcq-isCDGBAGWO8JmuWR0haIXbUSfBzzrOIXvIHcy-0ba4NXmtcD2mNm9ouW9p_9bX84vGGPFojOrGAEnzF9Mrx5n4gVS1dkul7PCiDqTrBClMs0k9Hyj_2lMnXKbKzioSsbfQx=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/tender-cash-generic-7fbe734b3c559d7d77d38935356e890f753759f58e707feb1d8e4635c62ec732.png\"\n" +
                "                                                                                    class=\"CToWUd\"></div>\n" +
                "                                                                        </td>\n" +
                "                                                                        <td align=\"right\" valign=\"top\" width=\"140\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:140px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#85898c;font-weight:normal;vertical-align:top;letter-spacing:0.2px;white-space:nowrap;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                08/09/2020, 12:24</div>\n" +
                "                                                                            <div\n" +
                "                                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:14px;line-height:21px;color:#85898c;font-weight:normal;vertical-align:top;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:3.5px;padding-right:0;padding-bottom:3.5px;padding-left:0\">\n" +
                "                                                                                #W8OC</div>\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                    <tr>\n" +
                "                                                                        <td align=\"left\" colspan=\"2\" valign=\"top\"\n" +
                "                                                                            width=\"140\"\n" +
                "                                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;width:140px;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                        </td>\n" +
                "                                                                    </tr>\n" +
                "                                                                </tbody>\n" +
                "                                                            </table>\n" +
                "                                                        </td>\n" +
                "                                                        <td bgcolor=\"#ffffff\" width=\"16\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img width=\"16\" height=\"1\" border=\"0\" alt=\"\"\n" +
                "                                                                style=\"line-height:0;font-size:0\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/itZ_paLI0DeLWhzjhd9_XfvLwnSHsyLhhHEb_Vw0POBUxzD-ZJH3-zZeF20XixwTAxZwoWbbxTRcsTqtEPs3Bevt9VesEwjd1mRKBto8WIFBvNpCdpeMkVenubBAlQxMtBYUH-0yC0d_UZvKA4PO0g7yJQ6ChOM9v_5AmRiuHUhTOwRN1e_qe0HG8w3wvs-c21HQ8mk=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/spacer-17af9e65317bbbfbbd0bcdc729f14faadf37cd08cf30cc0fe0b72443e78cbffb.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" ;

        String content18 =
                "                                            <table bgcolor=\"#f2f4f5\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <img alt=\"\" width=\"375\" height=\"4\"\n" +
                "                                                                style=\"border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0;display:block;max-width:375px;width:100%\"\n" +
                "                                                                src=\"https://ci6.googleusercontent.com/proxy/wMqBY75C6df4CG7j6eZifjlxyCQtSl3Vk-YG8RSB1L91zuE_OGidPDF0J6mXgoTWvFh8fHPhIUKLyS-AUebv4spH7DMuouJpT1idUyo7Ej_d6Kz8vfypZMrBDnfcosbOmpABK2y9_XCSQkY-jkB5x1oz7GC0Q77D4OD_Hn-G-jYsUsSP0kF6FLRaRZV6lF2W4fxKI_EelGiCB8w_cZOegqO1=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/receipt-bottom-edge-9546851a9ee332ff84de94e1862dc33629fdbd525713db3affdd72287acb5ae7.png\"\n" +
                "                                                                class=\"CToWUd\"></td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                            <table bgcolor=\"#f2f4f5\" cellpadding=\"0\" cellspacing=\"0\"\n" +
                "                                                style=\"border-collapse:collapse;border-spacing:0;border-top-width:0;border-right-width:0;border-bottom-width:0;border-left-width:0\">\n" +
                "                                                <tbody>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"24\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"10\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <div\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:12px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                © 2020 Square, Inc.</div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"10\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <div\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:12px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                1455 Market Street, Suite 600</div>\n" +
                "                                                            <div\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:12px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                San Francisco, CA 94103, USA</div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"10\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" ;

        String content19 =
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <div\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:12px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                <a id=\"m_7465221761897056103x_privacy-policy\"\n" +
                "                                                                    href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Fsquareup.com%2Ftr%2Fen%2Flegal%2Fprivacy/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/bD551xlARg-X2y46ATwYlnXPbHs=178\"\n" +
                "                                                                    style=\"text-decoration:underline!important;color:#85898c\"\n" +
                "                                                                    target=\"_blank\"\n" +
                "                                                                    data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Fsquareup.com%252Ftr%252Fen%252Flegal%252Fprivacy/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/bD551xlARg-X2y46ATwYlnXPbHs%3D178&amp;source=gmail&amp;ust=1599644761295000&amp;usg=AFQjCNFPLk0poR99F2gVmGTvjNO2h6MKFw\">Privacy\n" +
                "                                                                    Policy</a></div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"20\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <a id=\"m_7465221761897056103x_receipt-square-logo\"\n" +
                "                                                                href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Fsquareup.com/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/GLp7_yNkkwNk7Tgpv0L37qzR7OQ=178\"\n" +
                "                                                                style=\"text-decoration:normal!important;color:#85898c\"\n" +
                "                                                                target=\"_blank\"\n" +
                "                                                                data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Fsquareup.com/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/GLp7_yNkkwNk7Tgpv0L37qzR7OQ%3D178&amp;source=gmail&amp;ust=1599644761295000&amp;usg=AFQjCNH44xDIkeVBt2QV2SJzCs-dYjZbng\"><img\n" +
                "                                                                    width=\"24\" height=\"24\" border=\"0\" alt=\"Footer logo\"\n" +
                "                                                                    src=\"https://ci6.googleusercontent.com/proxy/3ZlgN1cMflRplt1xWeU5xKzYPzi0XC2iF7uglDCgXbpfKyDGMomVy_3Yq7eREfUCLvlbL7PM6mXUkg0AQPBptEasc7yFi-ZWfdCSQf4yyQ2pzFJ9UaWj2a5QrZ9fQ5PCN8UypiGHnMY-ISGANvUQJjnUPcjBGbJ1ubRlpAoI5WIP81ucUdE7nSyGiVPTxarC0b5L_-ZpCNi9TA=s0-d-e1-ft#https://d3g64w74of3jgu.cloudfront.net/receipts/assets/footer-logo-d354ee4f8b2a914ed1959eaa77323e1f444494f53a4d44f56b1abad8f028e8e8.png\"\n" +
                "                                                                    class=\"CToWUd\"></a></td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"30\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td align=\"center\" valign=\"top\"\n" +
                "                                                            style=\"line-height:0px;font-size:0px;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                            <div\n" +
                "                                                                style=\"font-family:SQMarket,HelveticaNeue,&quot;Helvetica Neue&quot;,Helvetica,Arial,sans-serif;font-size:12px;line-height:18px;color:#85898c;font-weight:normal;letter-spacing:0.2px;margin-top:0;margin-right:0;margin-bottom:0;margin-left:0;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                                <a href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Fwww.mapbox.com%2Fabout%2Fmaps%2F/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/ENQDPmOuoxMLS-7F_myWGODIDjk=178\"\n" +
                "                                                                    style=\"text-decoration:underline!important;color:#85898c\"\n" +
                "                                                                    target=\"_blank\"\n" +
                "                                                                    data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Fwww.mapbox.com%252Fabout%252Fmaps%252F/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/ENQDPmOuoxMLS-7F_myWGODIDjk%3D178&amp;source=gmail&amp;ust=1599644761296000&amp;usg=AFQjCNHOkEIjicKalnKBSloeacA3ojDu0Q\">©\n" +
                "                                                                    Mapbox</a> <a\n" +
                "                                                                    href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Fwww.openstreetmap.org%2Fcopyright/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/M7D3CX5fTVwzVh8cDJT1uArA-ks=178\"\n" +
                "                                                                    style=\"text-decoration:underline!important;color:#85898c\"\n" +
                "                                                                    target=\"_blank\"\n" +
                "                                                                    data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Fwww.openstreetmap.org%252Fcopyright/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/M7D3CX5fTVwzVh8cDJT1uArA-ks%3D178&amp;source=gmail&amp;ust=1599644761296000&amp;usg=AFQjCNGujk4XEkThKXGqrtqb3yYOOdRAIQ\">\n" +
                "                                                                    © OpenStreetMap</a> <a\n" +
                "                                                                    href=\"https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%2F%2Fwww.mapbox.com%2Fmap-feedback/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/WN61ZZzAmXkq8MuAVTGekQPX9dk=178\"\n" +
                "                                                                    style=\"text-decoration:underline!important;color:#85898c\"\n" +
                "                                                                    target=\"_blank\"\n" +
                "                                                                    data-saferedirecturl=\"https://www.google.com/url?q=https://rfrtpc7s.r.us-west-2.awstrack.me/L0/https:%252F%252Fwww.mapbox.com%252Fmap-feedback/1/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/WN61ZZzAmXkq8MuAVTGekQPX9dk%3D178&amp;source=gmail&amp;ust=1599644761296000&amp;usg=AFQjCNFd_pEiyymAtDJl2cqiljaYplrbGw\">\n" +
                "                                                                    Improve this map</a> </div>\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                    <tr>\n" +
                "                                                        <td width=\"100%\" height=\"30\" colspan=\"1\"\n" +
                "                                                            style=\"line-height:0;font-size:0;border-collapse:collapse;padding-top:0;padding-right:0;padding-bottom:0;padding-left:0\">\n" +
                "                                                        </td>\n" +
                "                                                    </tr>\n" +
                "                                                </tbody>\n" +
                "                                            </table>\n" +
                "                                        </td>\n" +
                "                                    </tr>\n" +
                "                                </tbody>\n" +
                "                            </table>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </tbody>\n" +
                "            </table>\n" +
                "        </div>\n" +
                "        <img alt=\"\" style=\"display:none;width:1px;height:1px\"\n" +
                "            src=\"https://ci4.googleusercontent.com/proxy/4Flcw1XFcVGOD4duHANzGVO_zrmHBDN7nX4cVz4Pq9QXKm-h849nSLoovaRG2-AgHzy9jk8DxKxrVSCltufSlkK1a1zNDjaQmJ5-f-chru1WCmLcedMf8ZFZjIypabriD2JZWj4v7TMKKFsdlbm9CgenUb6Jc6PkR44vgYl0AJEAS7rvGiq0D_QagJzfv_fVaqptv1CTq0VhVes=s0-d-e1-ft#http://rfrtpc7s.r.us-west-2.awstrack.me/I0/010101746d07f8c9-87dfb1dc-00b8-49c0-8f74-efffe8ff86db-000000/LTXVgTgQhZrKuPaKrfJccdUz_wA=178\"\n" +
                "            class=\"CToWUd\">\n" +
                "        <div class=\"yj6qo\"></div>\n" +
                "        <div class=\"adL\">\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        allText =  content1 +
                content2 +
                content3 +
                content4 +
                content5 +
                content6 +
                content7 +
                content8 +
                content9 +
                content10 +
                content11 +
                content12 +
                content13 +
                content14 +
                content15 +
                content16 +
                content17 +
                content18 +
                content19 ;

        /*Intent emailIntent = null;
        emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                Html.fromHtml(new StringBuilder()
                        .append("<p><b>Some Content</b></p>")
                        .append("<a>http://www.google.com</a>")
                        .append("<small><p>More content</p></small>")
                        .toString())
        );*/

        //String s = Html.fromHtml(allText).toString();


        //SpannedString spannedString = new SpannedString(Html.fromHtml(allText));
        //String htmlEncodedString = Html.toHtml(spannedString);

        return allText;
    }

}
