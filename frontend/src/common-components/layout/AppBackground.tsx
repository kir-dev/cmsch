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
        className="fixed inset-0 hidden h-screen w-screen bg-no-repeat bg-cover bg-center bg-background text-foreground md:block"
        style={{ backgroundImage }}
      ></div>
      <div
        className="fixed inset-0 block h-screen w-screen bg-no-repeat bg-cover bg-center bg-background text-foreground md:hidden"
        style={{ backgroundImage: mobileBackgroundImage }}
      ></div>
      <div className="text-foreground relative">{children}</div>
    </>
  )
}
