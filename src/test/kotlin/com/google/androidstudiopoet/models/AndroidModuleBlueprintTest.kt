package com.google.androidstudiopoet.models

import com.google.androidstudiopoet.input.*
import com.google.androidstudiopoet.testutils.*
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class AndroidModuleBlueprintTest {
    private val resourcesConfig0: ResourcesConfig = mock()

    @Test
    fun `blueprint create proper activity names`() {
        val blueprint = getAndroidModuleBlueprint(numOfActivities = 2)

        blueprint.activityNames.assertEquals(listOf("Activity0", "Activity1"))
    }

    @Test
    fun `blueprint creates proper flavors and dimensions`() {
        val dimension1 = "dim1"
        val dimension2 = "dim2"
        val flavorName1 = "flav1"
        val flavorName2 = "flav2"
        val flavorName3 = "flav3"
        val flavorConfigs = listOf(
                FlavorConfig(flavorName1, dimension1),
                FlavorConfig(flavorName2, dimension1),
                FlavorConfig(flavorName3, dimension2))
        val blueprint = getAndroidModuleBlueprint(productFlavorConfigs = flavorConfigs)

        blueprint.flavorDimensions!!.assertEquals(setOf(dimension1, dimension2))
        blueprint.productFlavors!!.assertEquals(setOf(
                Flavor(flavorName1, dimension1),
                Flavor(flavorName2, dimension1),
                Flavor(flavorName3, dimension2)
        ))
    }

    @Test
    fun `blueprint creates amount of flavors that set in "count" field of each FlavorConfig`() {
        val dimension = "dim"
        val flavorName = "flav"
        val expectedFlavorName0 = "flav0"
        val expectedFlavorName1 = "flav1"
        val expectedFlavorName2 = "flav2"
        val flavorCount = 3
        val flavorConfig = FlavorConfig(flavorName, dimension, flavorCount)

        val blueprint = getAndroidModuleBlueprint(productFlavorConfigs = listOf(flavorConfig))

        assertOn(blueprint) {
            blueprint.flavorDimensions!!.assertEquals(setOf(dimension))
            blueprint.productFlavors!!.assertEquals(setOf(
                    Flavor(expectedFlavorName0, dimension),
                    Flavor(expectedFlavorName1, dimension),
                    Flavor(expectedFlavorName2, dimension)
            ))
        }
    }

    @Test
    fun `blueprint creates activity blueprint with java class when java code exists`() {
        whenever(resourcesConfig0.layoutCount).thenReturn(1)

        val androidModuleBlueprint = getAndroidModuleBlueprint()

        assertOn(androidModuleBlueprint) {
            activityBlueprints.assertEquals(listOf(ActivityBlueprint(
                    "Activity0",
                    androidModuleBlueprint.resourcesBlueprint!!.layoutNames[0],
                    androidModuleBlueprint.packagePath,
                    androidModuleBlueprint.packageName,
                    androidModuleBlueprint.packagesBlueprint.javaPackageBlueprints[0].classBlueprints[0],
                    listOf())))
        }
    }

    @Test
    fun `blueprint creates activity blueprint with koltin class when java code doesn't exist`() {
        whenever(resourcesConfig0.layoutCount).thenReturn(1)

        val androidModuleBlueprint = getAndroidModuleBlueprint(
                javaClassCount = 0,
                javaMethodsPerClass = 0,
                javaPackageCount = 0
        )

        assertOn(androidModuleBlueprint) {
            activityBlueprints[0].classBlueprint.assertEquals(
                    androidModuleBlueprint.packagesBlueprint.kotlinPackageBlueprints[0].classBlueprints[0])
        }
    }

    @Test
    fun `hasDataBinding is false when data binding config is null`() {
        val androidModuleBlueprint = getAndroidModuleBlueprint(dataBindingConfig = null)

        androidModuleBlueprint.hasDataBinding.assertFalse()
    }

    @Test
    fun `hasDataBinding is false when data binding config has zero listener count`() {
        val androidModuleBlueprint = getAndroidModuleBlueprint(dataBindingConfig = DataBindingConfig(listenerCount = 0))

        androidModuleBlueprint.hasDataBinding.assertFalse()
    }

    @Test
    fun `hasDataBinding is true when data binding config has positive listener count`() {
        val androidModuleBlueprint = getAndroidModuleBlueprint(dataBindingConfig = DataBindingConfig(listenerCount = 2))

        androidModuleBlueprint.hasDataBinding.assertTrue()
    }

    @Test
    fun `hasDataBinding is false when data binding config has negative listener count`() {
        val androidModuleBlueprint = getAndroidModuleBlueprint(dataBindingConfig = DataBindingConfig(listenerCount = -1))

        androidModuleBlueprint.hasDataBinding.assertFalse()
    }

    @Test
    fun `minSdkVersion is passed from AndroidBuildConfig`() {
        val androidBuildConfig = AndroidBuildConfig(minSdkVersion = 7)
        val androidModuleBlueprint = getAndroidModuleBlueprint(androidBuildConfig = androidBuildConfig)

        androidModuleBlueprint.minSdkVersion.assertEquals(androidBuildConfig.minSdkVersion)
    }

    @Test
    fun `targetSdkVersion is passed from AndroidBuildConfig`() {
        val androidBuildConfig = AndroidBuildConfig(targetSdkVersion = 7)
        val androidModuleBlueprint = getAndroidModuleBlueprint(androidBuildConfig = androidBuildConfig)

        androidModuleBlueprint.targetSdkVersion.assertEquals(androidBuildConfig.targetSdkVersion)
    }

    @Test
    fun `compileSdkVersion is passed from AndroidBuildConfig`() {
        val androidBuildConfig = AndroidBuildConfig(compileSdkVersion = 7)
        val androidModuleBlueprint = getAndroidModuleBlueprint(androidBuildConfig = androidBuildConfig)

        androidModuleBlueprint.compileSdkVersion.assertEquals(androidBuildConfig.compileSdkVersion)
    }

    private fun getAndroidModuleBlueprint(
            name: String = "androidAppModule1",
            numOfActivities: Int = 1,
            resourcesConfig: ResourcesConfig = resourcesConfig0,
            projectRoot: String = "root",
            hasLaunchActivity: Boolean = true,
            useKotlin: Boolean = false,
            dependencies: List<ModuleDependency> = listOf(),
            productFlavorConfigs: List<FlavorConfig>? = null,
            buildTypeConfigs: List<BuildTypeConfig>? = null,
            javaPackageCount: Int = 1,
            javaClassCount: Int = 1,
            javaMethodsPerClass: Int = 1,
            kotlinPackageCount: Int = 1,
            kotlinClassCount: Int = 1,
            kotlinMethodsPerClass: Int = 1,
            extraLines: List<String>? = null,
            generateTests: Boolean = true,
            dataBindingConfig: DataBindingConfig? = null,
            androidBuildConfig: AndroidBuildConfig = AndroidBuildConfig()
    ) = AndroidModuleBlueprint(name, numOfActivities, resourcesConfig, projectRoot, hasLaunchActivity, useKotlin,
            dependencies, productFlavorConfigs, buildTypeConfigs, javaPackageCount, javaClassCount, javaMethodsPerClass,
            kotlinPackageCount, kotlinClassCount, kotlinMethodsPerClass, extraLines, generateTests, dataBindingConfig,
            androidBuildConfig)
}