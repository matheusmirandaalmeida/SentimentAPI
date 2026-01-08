from deep_translator import GoogleTranslator

def translate_commentary(input_text: str) -> str:
    try:
        translator = GoogleTranslator(source="auto", target="en")
        return translator.translate(input_text)
    except Exception:
        return input_text
