$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$releaseDir = Join-Path $root "releases-jar"
$java25Home = "C:\Program Files\Java\jdk-25.0.2"

$targets = @(
	@{ Minecraft = "1.21"; Yarn = "1.21+build.9"; FabricApi = "0.102.0+1.21"; ModMenu = "11.0.4"; Compat = "legacy"; LoaderDependency = "0.15.11"; JavaDependency = "21" },
	@{ Minecraft = "1.21.1"; Yarn = "1.21.1+build.3"; FabricApi = "0.116.12+1.21.1"; ModMenu = "11.0.4"; Compat = "legacy"; LoaderDependency = "0.15.11"; JavaDependency = "21" },
	@{ Minecraft = "1.21.2"; Yarn = "1.21.2+build.1"; FabricApi = "0.106.1+1.21.2"; ModMenu = "12.0.1"; Compat = "modern_1_21_2"; LoaderDependency = "0.16.7"; JavaDependency = "21" },
	@{ Minecraft = "1.21.3"; Yarn = "1.21.3+build.2"; FabricApi = "0.114.1+1.21.3"; ModMenu = "12.0.1"; Compat = "modern_1_21_2"; LoaderDependency = "0.16.8"; JavaDependency = "21" },
	@{ Minecraft = "1.21.4"; Yarn = "1.21.4+build.8"; FabricApi = "0.119.4+1.21.4"; ModMenu = "13.0.4"; Compat = "modern"; LoaderDependency = "0.16.9"; JavaDependency = "21" },
	@{ Minecraft = "1.21.5"; Yarn = "1.21.5+build.1"; FabricApi = "0.128.2+1.21.5"; ModMenu = "14.0.2"; Compat = "modern"; LoaderDependency = "0.16.10"; JavaDependency = "21" },
	@{ Minecraft = "1.21.6"; Yarn = "1.21.6+build.1"; FabricApi = "0.128.2+1.21.6"; ModMenu = "15.0.2"; Compat = "modern_1_21_6"; LoaderDependency = "0.16.13"; JavaDependency = "21" },
	@{ Minecraft = "1.21.7"; Yarn = "1.21.7+build.8"; FabricApi = "0.129.0+1.21.7"; ModMenu = "15.0.2"; Compat = "modern_1_21_6"; LoaderDependency = "0.16.13"; JavaDependency = "21" },
	@{ Minecraft = "1.21.8"; Yarn = "1.21.8+build.1"; FabricApi = "0.136.1+1.21.8"; ModMenu = "15.0.2"; Compat = "modern_1_21_6"; LoaderDependency = "0.16.13"; JavaDependency = "21" },
	@{ Minecraft = "1.21.9"; Yarn = "1.21.9+build.1"; FabricApi = "0.134.1+1.21.9"; ModMenu = "16.0.1"; Compat = "modern_1_21_9"; LoaderDependency = "0.17.0"; JavaDependency = "21" },
	@{ Minecraft = "1.21.10"; Yarn = "1.21.10+build.3"; FabricApi = "0.138.4+1.21.10"; ModMenu = "16.0.1"; Compat = "modern_1_21_9"; LoaderDependency = "0.17.0"; JavaDependency = "21" },
	@{ Minecraft = "1.21.11"; Yarn = "1.21.11+build.6"; FabricApi = "0.141.4+1.21.11"; ModMenu = "17.0.0"; Compat = "modern_1_21_11"; LoaderDependency = "0.17.3"; JavaDependency = "21" },
	@{ Minecraft = "26.1"; FabricApi = "0.145.1+26.1"; ModMenu = "18.0.0-beta.1"; Official = $true; OfficialSource = "official_26_1"; LoaderDependency = "0.18.4"; JavaDependency = "25" },
	@{ Minecraft = "26.1.1"; FabricApi = "0.145.4+26.1.1"; ModMenu = "18.0.0-beta.1"; Official = $true; OfficialSource = "official_26_1"; LoaderDependency = "0.18.4"; JavaDependency = "25" },
	@{ Minecraft = "26.1.2"; FabricApi = "0.152.1+26.1.2"; ModMenu = "18.0.0-beta.1"; Official = $true; OfficialSource = "official_26_1"; LoaderDependency = "0.18.4"; JavaDependency = "25" },
	@{ Minecraft = "26.2"; FabricApi = "0.152.2+26.2"; ModMenu = "20.0.0-beta.3"; Official = $true; OfficialSource = "official_26_2"; LoaderDependency = "0.18.4"; JavaDependency = "25" }
)

New-Item -ItemType Directory -Force -Path $releaseDir | Out-Null
Remove-Item -Path (Join-Path $releaseDir "*.jar") -Force -ErrorAction SilentlyContinue

Push-Location $root
try {
	foreach ($target in $targets) {
		Write-Host "Building Minecraft $($target.Minecraft)"
		if ($target.Official) {
			$java25Exe = Join-Path $java25Home "bin\java.exe"
			if (-not (Test-Path $java25Exe)) {
				throw "Java 25 was not found at $java25Home. Install Java 25 or update `$java25Home in this script."
			}

			$previousJavaHome = $env:JAVA_HOME
			$previousPath = $env:Path
			try {
				$env:JAVA_HOME = $java25Home
				$env:Path = "$java25Home\bin;$previousPath"
				& .\gradlew.bat -p gradle-26 clean build copyReleaseJar --no-daemon --console=plain `
					"-Pminecraft_version=$($target.Minecraft)" `
					"-Pfabric_api_version=$($target.FabricApi)" `
					"-Pmodmenu_version=$($target.ModMenu)" `
					"-Pofficial_source=$($target.OfficialSource)" `
					"-Ploader_dependency=$($target.LoaderDependency)" `
					"-Pjava_dependency=$($target.JavaDependency)" `
					"-Pminecraft_dependency=~$($target.Minecraft)"
			} finally {
				if ($null -eq $previousJavaHome) {
					Remove-Item Env:JAVA_HOME -ErrorAction SilentlyContinue
				} else {
					$env:JAVA_HOME = $previousJavaHome
				}
				$env:Path = $previousPath
			}
		} else {
			& .\gradlew.bat clean copyReleaseJar --console=plain `
				"-Pminecraft_version=$($target.Minecraft)" `
				"-Pyarn_mappings=$($target.Yarn)" `
				"-Pfabric_api_version=$($target.FabricApi)" `
				"-Pmodmenu_version=$($target.ModMenu)" `
				"-Pcompat_api=$($target.Compat)" `
				"-Ploader_dependency=$($target.LoaderDependency)" `
				"-Pjava_dependency=$($target.JavaDependency)" `
				"-Pminecraft_dependency=~$($target.Minecraft)"
		}

		if ($LASTEXITCODE -ne 0) {
			exit $LASTEXITCODE
		}
	}
} finally {
	Pop-Location
}

Write-Host "Release jars written to $releaseDir"
