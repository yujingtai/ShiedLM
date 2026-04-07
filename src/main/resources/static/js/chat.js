document.addEventListener("DOMContentLoaded", () => {
    const promptField = document.querySelector("#prompt");
    if (promptField) {
        promptField.focus();
    }

    document.querySelectorAll(".sample-use").forEach((button) => {
        button.addEventListener("click", () => {
            if (!promptField) {
                return;
            }

            promptField.value = button.dataset.prompt ?? "";
            promptField.focus();
            promptField.setSelectionRange(promptField.value.length, promptField.value.length);
        });
    });
});
