
    document.addEventListener("DOMContentLoaded", async () => {
    const trackingCodeInput = document.getElementById("currentTrackingCode");
    if (trackingCodeInput) {
    const trackingCode = trackingCodeInput.value;
    const timelineContainer = document.getElementById("trackingTimelineContainer");
    try {
    const response = await fetch(`/api/tracking/${trackingCode}`);
    if (!response.ok) throw new Error("Takip geçmişi alınamadı.");

    const history = await response.json();

    if (history.length === 0) {
    timelineContainer.innerHTML = "<p>Bu kargo için henüz bir hareket kaydı bulunmuyor.</p>";
    return;
}

    let html = '<div class="timeline">';
    history.forEach(log => {
    const dateObj = new Date(log.dateTime);
    const date = dateObj.toLocaleDateString('tr-TR') + ' - ' + dateObj.toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' });

    const statusMap = {
    'PENDING': 'Beklemede',
    'IN_TRANSIT': 'Yolda',
    'DELIVERED': 'Teslim Edildi',
    'CANCELLED': 'İptal Edildi'
};
    const trStatus = statusMap[log.status] || log.status;

    html += `<div class="timeline-item">
                            <div class="timeline-icon"></div>
                            <div class="timeline-content">
                                <div class="timeline-date">🕒 ${date}</div>
                                <div class="timeline-status">${trStatus}</div>
                                <div class="timeline-desc">${log.description}</div>
                            </div>
                         </div>`;
});
    timelineContainer.innerHTML = html + '</div>';

} catch (e) {
    timelineContainer.innerHTML = `<p style="color:red;">Hata: ${e.message}</p>`;
}
}
});
