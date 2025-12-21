document.addEventListener("DOMContentLoaded", () => {
    let activeRow = null;
    // Make each table row clickable

    document.getElementById("copybutton").addEventListener("click", async () => {
        const res = await fetch("/copy");
        const text = await res.text();
        await navigator.clipboard.writeText(text);
    });

    document.querySelectorAll("#mytable table tr").forEach((row, index) => {
        // Skip the header row
        if (index === 0) return;

        row.addEventListener("click", () => {
            activeRow = row;

            const date = row.dataset.date;
            const payee = row.dataset.payee;
            const debit = row.dataset.debit;
            const amount = row.dataset.amount;

            document.getElementById("pDate").textContent = date;
            document.getElementById("pPayee").value = payee;
            document.getElementById("pDebit").value = debit;
            document.getElementById("popup").style.display = "flex";
        });
    });

    // Close popup
    document.getElementById("close").onclick = () =>
        document.getElementById("popup").style.display = "none";


    document.getElementById("savePopup").onclick = () => {
        if (activeRow) {

            const p = document.getElementById("pPayee").value.trim();
            const c = document.getElementById("pDebit").value.trim();

            fetch("/mytest", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: `a=${encodeURIComponent(p)}&b=${encodeURIComponent(c)}`
            });
        }

        document.getElementById("popup").style.display = "none";
    };
});
