// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "ZkorumCapacitorSecureSigning",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "ZkorumCapacitorSecureSigning",
            targets: ["SecureSigningPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SecureSigningPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SecureSigningPlugin"),
        .testTarget(
            name: "SecureSigningPluginTests",
            dependencies: ["SecureSigningPlugin"],
            path: "ios/Tests/SecureSigningPluginTests")
    ]
)