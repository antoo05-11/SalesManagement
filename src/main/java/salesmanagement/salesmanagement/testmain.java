package salesmanagement.salesmanagement;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.ArrayList;
import java.util.List;

public class testmain {
    public static void main() {
        List<String> phoneCodes = new ArrayList<>();
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        for (String regionCode : phoneUtil.getSupportedRegions()) {
            System.out.println(regionCode);
        }
    }
}
