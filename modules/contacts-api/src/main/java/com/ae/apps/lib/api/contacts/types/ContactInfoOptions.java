package com.ae.apps.lib.api.contacts.types;

/**
 * Specifies options for getting ContactInfo
 */
public final class ContactInfoOptions {

    private final boolean includePhoneDetails;

    private final boolean includeContactPicture;

    private final int defaultContactPicture;

    private final boolean filterDuplicatePhoneNumbers;

    private ContactInfoOptions(Builder builder) {
        this.includePhoneDetails = builder.includePhoneDetails;
        this.defaultContactPicture = builder.defaultContactPicture;
        this.includeContactPicture = builder.includeContactPicture;
        this.filterDuplicatePhoneNumbers = builder.filterDuplicatePhoneNumbers;
    }

    public boolean isIncludePhoneDetails() {
        return includePhoneDetails;
    }

    public boolean isIncludeContactPicture() { return includeContactPicture;  }

    public boolean isFilterDuplicatePhoneNumbers() { return filterDuplicatePhoneNumbers; }

    public int getDefaultContactPicture() {
        return defaultContactPicture;
    }

    /**
     * Use ContactInfoOptions.Builder to create an instance of ContactInfoOptions
     *
     * Example usage:
     * <pre>
     *     ContactInfoOptions options = new ContactInfoOptions.Builder(true)
     *                     .includeContactPicture(true)
     *                     .defaultContactPicture(com.ae.apps.lib.R.drawable.profile_icon_1)
     *                     .build();
     * </pre>
     * @since 4.1
     */
    public static class Builder {
        private boolean includePhoneDetails;
        private boolean includeContactPicture;
        private int defaultContactPicture;
        private boolean filterDuplicatePhoneNumbers;

        public Builder(){
            // Can be used to create an empty Options object as well
        }

        public Builder includePhoneDetails(boolean includePhoneDetails) {
            this.includePhoneDetails = includePhoneDetails;
            return this;
        }

        public Builder includeContactPicture(boolean includeContactPicture) {
            this.includeContactPicture = includeContactPicture;
            return this;
        }

        public Builder defaultContactPicture(int defaultContactPicture) {
            this.defaultContactPicture = defaultContactPicture;
            return this;
        }

        public Builder filterDuplicatePhoneNumbers(boolean filterDuplicatePhoneNumbers) {
            this.filterDuplicatePhoneNumbers = filterDuplicatePhoneNumbers;
            return this;
        }

        public ContactInfoOptions build() {
            return new ContactInfoOptions(this);
        }

    }

}
