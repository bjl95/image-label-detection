load("@rules_java//java:defs.bzl", "java_binary", "java_library", "java_test")

package(default_visibility = ["//visibility:public"])

java_binary(
    name = "Main",
    srcs = glob(["src/main/java/*.java"]),
    deps = [
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:com_google_flogger_flogger",
        "@maven//:com_google_flogger_flogger_system_backend",
        "@maven//:com_google_cloud_google_cloud_vision",
        "@maven//:io_grpc_grpc_api",
        "@maven//:io_grpc_grpc_core",
        "@maven//:com_google_api_grpc_grpc_google_cloud_vision_v1",
        "@maven//:org_codehaus_mojo_animal_sniffer_annotations"
    ],
)

java_test(
    name = "tests",
    srcs = glob(["src/test/java/*.java", "src/main/java/*.java"]),
    test_class = "MainTest",
    deps = [
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:junit_junit",
        "@maven//:com_google_flogger_flogger",
        "@maven//:com_google_flogger_flogger_system_backend",
    ],
    data = glob(["src/test/resources/**"])
)