# QR image and token generator for CMSch

The output CSV is CMSch import compatible

![QR code output](images/qr.png)

## Example usage 

The style of the QR codes can be customized at [qr-code-styling.com](https://qr-code-styling.com/), this CLI uses the exported options

*Note: The image needs to be provided by a command line argument*

```bash
yarn start --count 100 --score 1 --frontendUrl="http://localhost:3000/home" --generateQrCodes=true --qrImage="path/to/image.svg" --qrCodeStyling="assets/DefaultOptions.json" --qrOutDirectory="output"
```

## All options

```bash
yarn start --help
```

