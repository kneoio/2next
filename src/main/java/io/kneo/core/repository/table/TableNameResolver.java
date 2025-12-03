package io.kneo.core.repository.table;

public class TableNameResolver implements ITableResolver{
    public static final String USER_ENTITY_NAME = "user";
    public static final String ROLE_ENTITY_NAME = "role";
    public static final String LANGUAGE_ENTITY_NAME = "lang";
    public static final String MODULE_ENTITY_NAME = "module";
    public static final String AGREEMENT_ENTITY_NAME = "agreement";
    public static final String USER_CONSENT_ENTITY_NAME = "user_consent";
    public static final String USER_BILLING_ENTITY_NAME = "user_billing";
    public static final String USER_SUBSCRIPTION_ENTITY_NAME = "user_subscription";
    public static final String SUBSCRIPTION_PRODUCT_ENTITY_NAME = "subscription_product";
    private static final String USER_TABLE_NAME = "_users";
    private static final String ROLE_TABLE_NAME = "_roles";
    private static final String LANGUAGES_TABLE_NAME = "_langs";
    private static final String MODULES_TABLE_NAME = "_modules";
    private static final String AGREEMENTS_TABLE_NAME = "_agreements";
    private static final String USER_CONSENTS_TABLE_NAME = "_user_consents";
    private static final String USER_BILLINGS_TABLE_NAME = "_user_billings";
    private static final String USER_SUBSCRIPTIONS_TABLE_NAME = "_user_subscriptions";
    private static final String SUBSCRIPTION_PRODUCTS_TABLE_NAME = "_subscription_products";
    protected static final String DEFAULT_SCHEMA = "public";

    public EntityData getEntityNames(String type) {
        return switch (type) {
            case USER_ENTITY_NAME ->new EntityData(DEFAULT_SCHEMA + "." + USER_TABLE_NAME, null);
            case ROLE_ENTITY_NAME ->new EntityData(DEFAULT_SCHEMA + "." + ROLE_TABLE_NAME, null);
            case LANGUAGE_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + LANGUAGES_TABLE_NAME, null);
            case MODULE_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + MODULES_TABLE_NAME, null);
            case AGREEMENT_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + AGREEMENTS_TABLE_NAME, null);
            case USER_CONSENT_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + USER_CONSENTS_TABLE_NAME, null);
            case USER_BILLING_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + USER_BILLINGS_TABLE_NAME, null);
            case USER_SUBSCRIPTION_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + USER_SUBSCRIPTIONS_TABLE_NAME, null);
            case SUBSCRIPTION_PRODUCT_ENTITY_NAME -> new EntityData(DEFAULT_SCHEMA + "." + SUBSCRIPTION_PRODUCTS_TABLE_NAME, null);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public static TableNameResolver create() {
        return new TableNameResolver();
    }

}
