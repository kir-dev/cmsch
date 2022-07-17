export default function (images: string | undefined, alts: string | undefined, urls: string | undefined) {
  const imageArray = (images || '').split(',')
  const altArray = (alts || '').split(',')
  const urlArray = (urls || '').split(',')
  return imageArray.map((img, index) => {
    return { image: img, alt: altArray[index], url: urlArray[index] }
  })
}
