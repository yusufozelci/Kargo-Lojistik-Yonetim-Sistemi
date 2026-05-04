document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    const trackingCode = urlParams.get('trackingNumber');

    if (trackingCode) {
        document.getElementById('tracking-result-container').style.display = 'block';
        document.getElementById('display-tracking-code').innerText = trackingCode;
        const timelineContainer = document.getElementById('tracking-timeline');
        timelineContainer.innerHTML = '<div class="text-center text-muted">Kargo bilgileri yükleniyor...</div>';

        fetch(`/api/tracking/${trackingCode}`)
            .then(response => {
                if (!response.ok) throw new Error("Bu takip numarasına ait bir kargo bulunamadı veya henüz işlem görmedi.");
                return response.json();
            })
            .then(data => {
                timelineContainer.innerHTML = '';

                if(data.length === 0) {
                    timelineContainer.innerHTML = '<p class="text-center text-muted fw-bold">Henüz kargo hareketi bulunmuyor.</p>';
                    return;
                }

                data.forEach((event, index) => {
                    const isDelivered = event.status === 'DELIVERED';
                    const isFirst = index === 0;

                    const date = new Date(event.dateTime);
                    const formattedDate = date.toLocaleDateString('tr-TR', { day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit' });

                    let markerClass = isDelivered ? 'bg-success' : (isFirst ? 'bg-primary shadow-sm border-primary' : 'bg-secondary');
                    let textClass = isDelivered ? 'text-success' : 'text-dark';

                    const html = `
                        <div class="timeline-item ${index === data.length - 1 ? 'border-0 pb-0' : ''}">
                            <div class="timeline-marker border border-4 border-white ${markerClass}"></div>
                            <h6 class="fw-bold mb-1 ${textClass}">${event.status}</h6>
                            <p class="text-muted small mb-0">
                                <span>${event.description}</span> <br>
                                <span class="text-primary fw-medium">${formattedDate}</span>
                            </p>
                        </div>
                    `;
                    timelineContainer.innerHTML += html;
                });
            })
            .catch(error => {
                timelineContainer.innerHTML = `<div class="alert alert-danger shadow-sm text-center">${error.message}</div>`;
            });
    }
});