
async function createDebateCallFrame(callWrapper) {
    return window.DailyIframe.createFrame(callWrapper, {
        iframeStyle: {
            height: '100%',
            width: '100%',
            aspectRatio: 16 / 9,
            minWidth: '500px',
            maxWidth: '920px',
            border: '0',
            borderRadius: '12px',
        },
    });
}