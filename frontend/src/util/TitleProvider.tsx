import { createContext, type FC, type PropsWithChildren, useContext, useEffect, useState } from 'react'

export interface TitleContext {
  addTitle: (title: string) => void
  removeTitle: (title: string) => void
}
// eslint-disable-next-line react-refresh/only-export-components
export const TitleContext = createContext<TitleContext>({ addTitle: () => {}, removeTitle: () => {} })

export const Title = ({ text }: { text: string | undefined }) => {
  const titleContext = useContext(TitleContext)
  useEffect(() => {
    if (!text) return

    titleContext.addTitle(text)
    return () => titleContext.removeTitle(text)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [text])

  return null
}

export const TitleProvider: FC<PropsWithChildren & { titleTemplate: (title: string) => string }> = ({ children, titleTemplate }) => {
  const [titles, setTitles] = useState<string[]>([])

  return (
    <TitleContext.Provider
      value={{
        addTitle: (title: string) => setTitles([...titles, title]),
        removeTitle: (title: string) => setTitles(titles.filter((e) => e !== title))
      }}
    >
      <title>{titleTemplate(titles[titles.length - 1])}</title>
      {children}
    </TitleContext.Provider>
  )
}
