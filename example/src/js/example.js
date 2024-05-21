import { SecureSigning } from '@zkorum&#x2F;capacitor-secure-signing';

window.testEcho = () => {
  const inputValue = document.getElementById('echoInput').value;
  SecureSigning.echo({ value: inputValue });
};
