from pydantic import AnyHttpUrl, Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    OLLAMA_BASE_URL: AnyHttpUrl = Field(default="http://localhost:11434")
    OLLAMA_MODEL: str = Field(..., min_length=1)
    SPRING_BOOT_API_URL: AnyHttpUrl
    MAX_QUERY_LENGTH: int = Field(1024, gt=0, le=4096)
    MIN_QUERY_LENGTH: int = Field(2, gt=0)
    MAX_CART_QUANTITY: int = Field(100, gt=0)
    MAX_HISTORY_MESSAGES: int = Field(20, gt=0)


settings = Settings()


# **Comments:**
# * This defines a **centralized configuration class** using Pydantic’s `BaseSettings`, which automatically reads values from environment variables.
# * `OLLAMA_MODEL` is required (`...`) and must be a non-empty string—ensures your LLM model is always configured.
# * `SPRING_BOOT_API_URL` is validated as a proper URL using `AnyHttpUrl`, preventing invalid backend endpoints.
# * Query-related limits (`MIN_QUERY_LENGTH`, `MAX_QUERY_LENGTH`) enforce input constraints and protect your system from overly short or excessively long inputs.
# * `MAX_CART_QUANTITY` caps how many items can be added to the cart in a single request, avoiding abuse or unrealistic values.
# * The `Field(...)` definitions add validation rules (like min/max bounds), making the config safer and self-documented.
# * `Config` tells Pydantic to load variables from a `.env` file with UTF-8 encoding, simplifying local development and deployment.
# * `settings = Settings()` instantiates the config once, so it can be imported and reused across the application as a **singleton-like config object**.
# * Overall, this pattern ensures **type-safe, validated, and environment-driven configuration management**.
