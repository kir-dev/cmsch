import { useStyle } from '@/api/contexts/config/ConfigContext.tsx'
import { useColorModeValue } from '@/util/core-functions.util'
import type { FC, PropsWithChildren } from 'react'

export const AppBackground: FC<PropsWithChildren> = ({ children }) => {
  const theme = useStyle()
  const backgroundImage = useColorModeValue(`url(${theme?.lightBackgroundUrl})`, `url(${theme?.darkBackgroundUrl})`)
  const mobileBackgroundImage = useColorModeValue(`url(${theme?.lightMobileBackgroundUrl})`, `url(${theme?.darkMobileBackgroundUrl})`)
  return (
    <>
      <div
        className="fixed inset-0 -z-[9999999] h-screen w-screen bg-no-repeat bg-cover bg-center bg-background text-foreground"
        style={{ backgroundImage: window.innerWidth < 768 ? mobileBackgroundImage : backgroundImage }}
      ></div>
      <div className="text-foreground">{children}</div>
    </>
  )
}
