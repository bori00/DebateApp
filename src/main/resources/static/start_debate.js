function copyDebateCode() {
    const code = document.getElementById('copy-code');
    const copyButton = document.getElementById('copy-code-button');
    code.select();
    document.execCommand('copy');
    copyButton.innerHTML = 'Copied!';
}