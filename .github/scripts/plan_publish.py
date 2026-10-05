import json
import os
import subprocess
import tomllib
import urllib.request

with open("stonecutter.properties.toml", "rb") as f:
    props = tomllib.load(f)

mod = props["mod"]
targets = [key for key, value in props.items() if isinstance(value, dict) and key != "mod"]
tag = f"v{mod['version']}"


def jar_name(target):
    return f"{mod['id']}-{mod['version']}+{target}.jar"


# Modrinth reuses the same version number for every Minecraft target, so match on jar filename instead.
request = urllib.request.Request(
    f"https://api.modrinth.com/v2/project/{mod['modrinth_id']}/version",
    headers={"User-Agent": "LaryIsland/ScreenFX publish workflow"},
)
with urllib.request.urlopen(request) as response:
    on_modrinth = {file["filename"] for version in json.load(response) for file in version["files"]}

release = subprocess.run(
    ["gh", "release", "view", tag, "--json", "assets", "--jq", ".assets[].name"],
    capture_output=True,
    text=True,
)
if release.returncode == 0:
    on_github = set(release.stdout.split())
elif "release not found" in release.stderr:
    on_github = set()
else:
    raise SystemExit(f"Could not read GitHub release {tag}: {release.stderr}")

# CurseForge has no public lookup, so it is published alongside Modrinth and shares its check.
modrinth = [t for t in targets if jar_name(t) not in on_modrinth]
github = [t for t in targets if jar_name(t) not in on_github]
github_files = [f"versions/{t}/build/libs/{jar_name(t)}" for t in github]

with open(os.environ["GITHUB_OUTPUT"], "a") as out:
    out.write(f"tag={tag}\n")
    out.write(f"title={mod['name']} {tag}\n")
    out.write(f"modrinth={' '.join(modrinth)}\n")
    out.write(f"github={' '.join(github)}\n")
    out.write(f"github_files={' '.join(github_files)}\n")

summary = [
    f"## Publish plan for {mod['name']} {tag}",
    "| Target | Modrinth + CurseForge | GitHub release |",
    "|---|---|---|",
]
for t in targets:
    summary.append(f"| {t} | {'publish' if t in modrinth else 'done'} | {'publish' if t in github else 'done'} |")
with open(os.environ["GITHUB_STEP_SUMMARY"], "a") as out:
    out.write("\n".join(summary) + "\n")
print("\n".join(summary))
