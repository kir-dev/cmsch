import { useColorModeValue } from '@/util/core-functions.util'
import type { Organization } from '@/util/views/organization'
import { ChevronRight } from 'lucide-react'
import { Link } from 'react-router'

type CardListItemProps = {
  data: Organization
  link: string
}

export const CardListItem = ({ data, link }: CardListItemProps) => {
  let logoSource
  const logoColor = useColorModeValue(data.logo, data.darkLogo)
  if (data.logo) {
    logoSource = data.darkLogo ? logoColor : data.logo
  } else {
    logoSource = data.darkLogo
  }
  return (
    <Link to={link}>
      <div
        className={
          'rounded-lg p-3 md:p-4 mt-3 md:mt-5 transition-all duration-200 ease-in-out hover:translate-x-2 border ' +
          'hover:bg-secondary/80 bg-secondary text-secondary-foreground dark:bg-secondary dark:text-secondary-foreground'
        }
      >
        <div className="flex flex-row items-center space-x-3 md:space-x-4">
          {logoSource && (
            <div className="bg-white rounded-full p-2 md:p-4 flex items-center justify-center shrink-0 border shadow-sm">
              <img
                src={logoSource}
                alt={data.name}
                className="min-w-[2.5rem] sm:min-w-[3rem] md:min-w-[4rem] h-10 sm:h-12 md:h-16 w-10 object-contain"
              />
            </div>
          )}
          <div className="flex flex-col items-start overflow-hidden flex-1 space-y-0 md:space-y-1">
            <h3 className="text-sm md:text-xl font-bold truncate w-full">{data.name}</h3>
            {data.shortDescription && <p className="max-w-full hidden md:block text-sm line-clamp-2">{data.shortDescription}</p>}
          </div>
          <div className="flex-1 shrink-0" />
          <ChevronRight className="h-6 md:h-10 lg:h-16 w-6 md:w-10 lg:w-16 text-border shrink-0" />
        </div>
      </div>
    </Link>
  )
}
