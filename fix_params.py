import os
import re

def fix_params(path):
    try:
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()
    except Exception as e:
        print(f"Error reading {path}: {str(e)}")
        return False

    def add_final(match):
        pre_word = match.group(1)
        spacing = match.group(2)
        params_str = match.group(3)
        suffix = match.group(4)
        
        # Keywords to skip
        skip_keywords = {"if", "for", "while", "switch", "catch", "new", "synchronized", "super", "this"}
        if pre_word in skip_keywords:
            return match.group(0)
            
        # Check if it is a record header
        # We look back in the content for "record" keyword
        start_index = match.start()
        # Look back up to 100 chars
        look_back = content[max(0, start_index-100):start_index]
        if re.search(r"\brecord\b\s+\w+\s*$", look_back + (pre_word if pre_word else "")):
            # It is a record header. In records, components are implicitly final.
            # Adding "final" is a syntax error.
            # We should actually REMOVE "final" if we added it previously.
            params = []
            current = ""
            depth = 0
            for char in params_str:
                if char == "<": depth += 1
                elif char == ">": depth -= 1
                if char == "," and depth == 0:
                    params.append(current)
                    current = ""
                else:
                    current += char
            params.append(current)
            
            new_params = []
            changed_record = False
            for p in params:
                if "final " in p:
                    new_params.append(p.replace("final ", ""))
                    changed_record = True
                else:
                    new_params.append(p)
            if changed_record:
                return (pre_word if pre_word else "") + spacing + "(" + ",".join(new_params) + ")" + suffix
            return match.group(0)
            
        if not params_str.strip():
            return match.group(0)
            
        params = []
        current = ""
        depth = 0
        for char in params_str:
            if char == "<": depth += 1
            elif char == ">": depth -= 1
            if char == "," and depth == 0:
                params.append(current)
                current = ""
            else:
                current += char
        params.append(current)
        
        new_params = []
        changed = False
        for p in params:
            p_strip = p.strip()
            if not p_strip:
                new_params.append(p)
                continue
            
            if re.search(r"\bfinal\b", p_strip) or p_strip.endswith(" this"):
                new_params.append(p)
                continue
            
            annot_match = re.match(r"^(\s*(?:@[\w\.]+(?:\([^)]*\))?\s+)*)(.*)$", p_strip, re.DOTALL)
            if annot_match:
                annots = annot_match.group(1)
                rest = annot_match.group(2)
                if " " in rest.strip() or "\n" in rest.strip():
                    if not re.search(r"\bfinal\b", rest):
                        new_p = p.replace(p_strip, f"{annots}final {rest}")
                        new_params.append(new_p)
                        changed = True
                    else:
                        new_params.append(p)
                else:
                    new_params.append(p)
            else:
                new_params.append(p)
        
        if changed:
            return (pre_word if pre_word else "") + spacing + "(" + ",".join(new_params) + ")" + suffix
        return match.group(0)

    full_regex = r"(\b\w+)?(\s*)\(([\s\S]*?)\)(\s*(?:throws\s+[\w\.,\s\n]+)?\s*[\{;])"
    
    new_content = re.sub(full_regex, add_final, content)
    
    if content != new_content:
        try:
            with open(path, "w", encoding="utf-8") as f:
                f.write(new_content)
            return True
        except Exception as e:
            print(f"Error writing {path}: {str(e)}")
            return False
    return False

projects = ["borshchevyk.user", "borshchevyk.message"]
fixed_count = 0
for project in projects:
    project_path = os.path.join(os.getcwd(), project)
    for root, dirs, files in os.walk(project_path):
        for file in files:
            if file.endswith(".java"):
                if fix_params(os.path.join(root, file)):
                    fixed_count += 1
print(f"Fixed/Cleaned {fixed_count} files.")
