export const parseTopMessage = (topMessage: string) => {
  const splits = topMessage.split(/(\[\[.*?\]\])/g)
  const coloredComponents = splits.map((partial, idx) => {
    if (partial.match(/(\[\[.*?\]\])/)) {
      const coloredText = partial.substring(2, partial.length - 2)
      return (
        <span key={idx} id={coloredText} className="text-primary">
          {coloredText}
        </span>
      )
    } else {
      return <span key={idx}>{partial}</span>
    }
  })
  return <>{coloredComponents}</>
}
