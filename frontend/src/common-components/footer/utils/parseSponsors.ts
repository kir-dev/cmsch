export default function (images: string | undefined, alts: string | undefined, urls: string | undefined) {
  const imageArray = (images || '').split(',').map((image) => image.trim())
  const altArray = (alts || '').split(',').map((alt) => alt.trim())
  const urlArray = (urls || '').split(',').map((url) => url.trim())
  return imageArray.flatMap((image, index) => (image ? [{ image, alt: altArray[index] || '', url: urlArray[index] || '' }] : []))
}
