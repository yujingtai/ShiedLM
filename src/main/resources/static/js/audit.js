document.addEventListener("DOMContentLoaded", () => {
    const dialog = document.getElementById("audit-detail-dialog");
    const dialogBody = dialog?.querySelector("[data-dialog-body]");
    const closeButton = dialog?.querySelector("[data-dialog-close]");
    const triggers = document.querySelectorAll(".detail-trigger");

    if (!dialog || !dialogBody || triggers.length === 0) {
        return;
    }

    const closeDialog = () => {
        if (typeof dialog.close === "function" && dialog.open) {
            dialog.close();
        }
    };

    triggers.forEach((trigger) => {
        trigger.addEventListener("click", () => {
            const targetId = trigger.getAttribute("data-modal-target");
            if (!targetId) {
                return;
            }
            const content = document.getElementById(targetId);
            if (!content) {
                return;
            }
            dialogBody.innerHTML = content.innerHTML;
            if (typeof dialog.showModal === "function") {
                dialog.showModal();
            }
        });
    });

    closeButton?.addEventListener("click", closeDialog);

    dialog.addEventListener("click", (event) => {
        const rect = dialog.getBoundingClientRect();
        const clickedOutside = event.clientX < rect.left
                || event.clientX > rect.right
                || event.clientY < rect.top
                || event.clientY > rect.bottom;
        if (clickedOutside) {
            closeDialog();
        }
    });

    dialog.addEventListener("cancel", (event) => {
        event.preventDefault();
        closeDialog();
    });
});
