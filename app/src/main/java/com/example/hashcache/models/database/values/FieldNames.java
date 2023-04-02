package com.example.hashcache.models.database.values;

/**
 * Names of all the fields in the Firestore database
 */
public enum FieldNames {
    GENERATED_NAME("generatedName"),
    GENERATED_SCORE("generatedScore"),
    CODE_LOCATION_ID("codeLocationId"),
    SCANNABLE_CODE_ID("scannableCodeId"),
    COMMENT_BODY("body"),
    USERNAME("username"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    RECORD_GEOLOCATION("recordGeoLocation"),
    USER_ID("userId"),
    QR_COUNT("qrCount"),
    MAX_SCORE("maxScore"),
    TOTAL_SCORE("totalScore"),
    DEVICE_ID("deviceId"),
    COMMENTATOR_ID("commentatorId"),
    Geohash("geohash"),
    ScannableCodeId("scannableCodeId"),
    ImageBase64("base64Image"),
    Latitude("lat"),
    Longitude("lon"),
    HasLocation("hasLocation");

    public final String fieldName;
    public final String name;
    private FieldNames(String fieldName) {

        this.fieldName = fieldName;
        this.name = fieldName;
    }
}
