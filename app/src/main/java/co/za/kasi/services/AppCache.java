package co.za.kasi.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.za.kasi.model.superApp.a.waybillData.ScanParcel;
import co.za.kasi.model.superApp.a.waybillData.Waybills;
import co.za.kasi.model.superApp.a.waybillData.DeliveryCondition;

public class AppCache {

    public static String profileBase64;

    public static String ownerId;

    public static String username;

    public static String signatureBase64 ;

    public static String receiverName ;

    private static Map<String, String> signatureMap = new HashMap<>();

    public static Waybills currentWaybill;

    public static List<ScanParcel> scanParcelList;

    public static List<String> consolidatedWaybills;

    public static List<String> getParcelsScanned() {
        return parcelsScanned;
    }

    public static void setParcelsScanned(List<String> parcelsScanned) {
        AppCache.parcelsScanned = parcelsScanned;
    }

    public static List<String> parcelsScanned;

    public static List<Waybills> currentWaybillList;

    public static List<DeliveryCondition> deliveryConditions = new ArrayList<>();

    public static String getProfileBase64() {
        return profileBase64;
    }

    public static void setProfileBase64(String profileBase64) {
      AppCache.profileBase64 = profileBase64;
    }


    public static String getOwnerId() {
        return ownerId;
    }

    public static void setOwnerId(String ownerId) {
        AppCache.ownerId = ownerId;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        AppCache.username = username;
    }

    public static Waybills getCurrentWaybill() {
        return currentWaybill;
    }

    public static void setCurrentWaybill(Waybills currentWaybill) {
        AppCache.currentWaybill = currentWaybill;
    }

    public static List<ScanParcel> getScanParcelList() {
        return scanParcelList;
    }

    public static void setScanParcelList(List<ScanParcel> scanParcelList) {
        AppCache.scanParcelList = scanParcelList;
    }

    public static List<DeliveryCondition> getDeliveryConditions() {
        return deliveryConditions;
    }

    public static void setDeliveryConditions(List<DeliveryCondition> deliveryConditions) {
        AppCache.deliveryConditions = deliveryConditions;
    }

    public static void addDeliveryCondition(DeliveryCondition condition) {
        deliveryConditions.add(condition);
    }

    public static String getSignatureBase64() {
        return signatureBase64;
    }

    public static void setSignatureBase64(String signatureBase64) {
        AppCache.signatureBase64 = signatureBase64;
    }

    public static List<String> getConsolidatedWaybills() {
        return consolidatedWaybills;
    }

    public static void setConsolidatedWaybills(List<String> consolidatedWaybills) {
        AppCache.consolidatedWaybills = consolidatedWaybills;
    }

    public static List<Waybills> getCurrentWaybillList() {
        return currentWaybillList;
    }

    public static void setCurrentWaybillList(List<Waybills> currentWaybillList) {
        AppCache.currentWaybillList = currentWaybillList;
    }

    public static void setSignatureForWaybill(String waybillNumber, String signatureBase64) {
        if (signatureMap == null) {
            signatureMap = new HashMap<>();
        }
        signatureMap.put(waybillNumber, signatureBase64);
    }

    public static String getSignatureForWaybill(String waybillNumber) {
        if (signatureMap != null && signatureMap.containsKey(waybillNumber)) {
            return signatureMap.get(waybillNumber);
        }
        return null;
    }


    public static String getReceiverName() {
        return receiverName;
    }

    public static void setReceiverName(String receiverName) {
        AppCache.receiverName = receiverName;
    }
}
