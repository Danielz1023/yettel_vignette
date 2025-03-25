# Yettel Vignette App - Android Application

## Summary

The Yettel Vignette app lets users choose and buy national or county vignettes. After selecting a vignette, users can confirm their order and receive a success notification.

## Setup and Installation

1.  Clone the repository.
2.  **Start the server:** Navigate to the `/api` directory and run `docker-compose up`.
3.  Open in Android Studio.
4.  Sync Gradle.
5.  Run on emulator or device.

## Running on a Physical Device

To run the application on a physical device, you need to configure the network settings to allow communication with your local development server:

## Testing on Physical Device

1.  **Network Security Config:**
    * **Edit** `res/xml/network-security-config.xml`:

        ```
        <?xml version="1.0" encoding="utf-8"?>
        <network-security-config>
            <domain-config cleartextTrafficPermitted="true">
                <domain includeSubdomains="true">10.0.2.2</domain>
                <domain includeSubdomains="true">YOUR_DEVICE_IP</domain>
            </domain-config>
        </network-security-config>
        ```

    * **Replace** `YOUR_DEVICE_IP` with your device's IP address.

2.  **API URL:**
    * **Edit** the network setup file (`VignetteApiModule`) to use the correct IP address.

        ```kotlin
        private val BASE_URL = if (isEmulator()) {
            "http://10.0.2.2:8080/"
        } else {
            "http://YOUR_DEVICE_IP:8080/"
        }
        ```

    * **Replace** `YOUR_DEVICE_IP` with your device's IP address.ss.

## Future Improvements

* Move hardcoded strings from layouts to `dimens.xml`.
* Handle loading state in the activity
* Standardize layout naming conventions for consistency.
* Consider migrating UI to Jetpack Compose.
* Enhance error handling (network errors, API failures, etc.).
* Implement proper fragment lifecycle management.
* Improve RecyclerView performance (e.g., using DiffUtil, ViewHolders efficiently).

## API Improvement Suggestions

    ```json
    {
      "meta": {
        "requestId": "19577295",
        "status": "OK",
        "dataType": "HighwayTransaction"
      },
      "data": {
        "countryVignettes": [
          {
            "type": "DAY",
            "vehicleCategory": "CAR",
            "pricing": {
              "cost": 5150,
              "transactionFee": 200,
              "total": 5350
            }
          },
          // ... other country vignettes
        ],
        "countyVignettes": [
          {
            "type": "YEAR",
            "vehicleCategory": "CAR",
            "applicableCounties": [
              { "id": "YEAR_11", "name": "Bács-Kiskun" },
              // ... other counties
            ],
            "pricing": {
              "cost": 6660,
              "transactionFee": 200,
              "total": 6860
            }
          },
          // ... other county vignettes
        ],
        "vehicleCategories": {
          "CAR": {
            "vignetteCategory": "D1",
            "names": { "hu": "Személygépjármű", "en": "Car" }
          }
          // ... other vehicle categories
        }
      }
    }
    ```