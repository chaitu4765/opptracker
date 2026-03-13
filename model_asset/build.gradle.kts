plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("model_asset")
    dynamicDelivery {
        deliveryType.set("on-demand")
    }
}
