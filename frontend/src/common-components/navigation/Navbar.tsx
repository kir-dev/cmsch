import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useColorModeValue } from '@/util/core-functions.util'
import { Menu, X } from 'lucide-react'
import { useState } from 'react'
import { Link } from 'react-router'
import CurrentEventCard from '../CurrentEventCard'
import { ColorModeSwitcher } from './ColorModeSwitcher'
import { DesktopNav } from './desktop/DesktopNav'
import { MobileNav } from './mobile/MobileNav'

export const Navbar = () => {
  const [isOpen, setIsOpen] = useState(false)
  const onToggle = () => setIsOpen(!isOpen)
  const components = useConfigContext()?.components
  const logoUrl = useColorModeValue(components?.style?.lightLogoUrl, components?.style?.darkLogoUrl)
  const backdropFilter = useColorModeValue(components?.style?.lightNavbarFilter, components?.style?.darkNavbarFilter)
  const background = useColorModeValue(components?.style?.lightNavbarColor, components?.style?.darkNavbarColor)
  const textColor = useColorModeValue(components?.style?.lightTextColor, components?.style?.darkTextColor)
  return (
    <div
      className="mx-auto w-full max-w-full md:max-w-[64rem] font-heading mb-4 md:rounded-b-xl"
      style={{ backdropFilter, backgroundColor: background, color: textColor }}
    >
      <div
        className={'flex items-center min-h-[3rem] md:min-h-[4.5rem] py-2 px-4 mx-auto w-full max-w-full md:max-w-[56rem] lg:max-w-[72rem]'}
      >
        <div className="flex flex-1 -ml-2 md:ml-0 md:hidden">
          <button
            onClick={onToggle}
            className="p-2 inline-flex items-center justify-center rounded-md hover:bg-accent transition-colors"
            aria-label="Navigáció megnyitása"
          >
            {isOpen ? <X className="h-5 w-5" /> : <Menu className="h-5 w-5" />}
          </button>
        </div>
        <div className="flex justify-center md:justify-start">
          <Link to="/">
            {logoUrl ? (
              <img className="max-h-16 max-w-[7rem] object-contain" src={logoUrl} alt={components?.app?.siteName} />
            ) : (
              <h1 className="text-2xl font-bold my-2" style={{ fontFamily: components?.style?.displayFontName }}>
                {components?.app?.siteName}
              </h1>
            )}
          </Link>
        </div>
        <div className="hidden md:flex flex-1 justify-center md:justify-end">
          <div className="hidden md:flex mx-4">
            <DesktopNav />
          </div>
        </div>
        <div className="flex flex-1 md:flex-none -mr-2 md:mr-0 justify-end">
          {!components?.style?.forceDarkMode && <ColorModeSwitcher color={textColor} />}
        </div>
      </div>
      {/*The method in onClick hides the menu items when a menu item is clicked. Works for collapsible items too!*/}
      {isOpen && (
        <div
          onClick={(evt) => {
            if ((evt.target as Element).closest('.navitem')) onToggle()
          }}
          className="md:hidden"
        >
          <MobileNav />
        </div>
      )}
      {!!components?.event && <CurrentEventCard />}
    </div>
  )
}
