import os
import re

def fix_records(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()
    except:
        return False
    
    def remove_final_from_header(match):
        header = match.group(0)
        # Be careful not to replace final if it is part of another word, 
        # though final is a keyword.
        # Also remove it multiple times if needed.
        new_header = re.sub(r"\bfinal\s+", "", header)
        return new_header

    # Match record header: record Name(...)
    # We use [^)]* to match everything inside parentheses. 
    # This might fail if there are nested parentheses (e.g. in annotations),
    # but for record headers it is usually fine.
    new_content = re.sub(r"\brecord\s+\w+\s*\([\s\S]*?\)\s*(?=\{|\b)", remove_final_from_header, content)
    
    if content != new_content:
        with open(path, "w", encoding="utf-8") as f:
            f.write(new_content)
        return True
    return False

projects = ["borshchevyk.user", "borshchevyk.message"]
count = 0
for project in projects:
    project_path = os.path.join(os.getcwd(), project)
    for root, dirs, files in os.walk(project_path):
        for file in files:
            if file.endswith(".java"):
                if fix_records(os.path.join(root, file)):
                    count += 1
print(f"Cleaned {count} records.")
