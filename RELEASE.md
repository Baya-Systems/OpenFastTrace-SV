# Release Process

This repository uses GitHub Actions to automatically create releases when version tags are pushed.

## Creating a Release

1. **Update the version** (optional): The release workflow will automatically update the version in `build.gradle` to match the tag, but you can also update it manually beforehand.

2. **Create and push a version tag**:

   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

3. **The workflow will automatically**:
   - Build the project with the new version
   - Run all tests
   - Create a GitHub release
   - Upload the JAR file as a release asset

## Tag Format

Tags must follow the pattern `vX.Y.Z` where:

- `X` is the major version
- `Y` is the minor version  
- `Z` is the patch version

Examples: `v1.0.0`, `v2.1.3`, `v0.4.1`

## Release Assets

Each release will include:

- `systemverilog-importer-X.Y.Z.jar` - The main JAR file for use with OpenFastTrace

## Workflow Details

The release workflow (`.github/workflows/release.yml`):

- Triggers on tags matching `v*.*.*`
- Uses Java 17 and Gradle
- Builds and tests the project
- Creates a GitHub release with generated release notes
- Uploads the JAR file with a version-specific name

## Manual Release

If you need to create a release manually:

1. Go to the [Releases page](../../releases)
2. Click "Create a new release"
3. Create a new tag following the `vX.Y.Z` format
4. Add release notes
5. Upload the JAR file from `build/libs/`
