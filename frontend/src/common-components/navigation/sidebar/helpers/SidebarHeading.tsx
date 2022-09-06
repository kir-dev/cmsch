import { Heading, HStack, Image } from '@chakra-ui/react'
import { useColorModeValue } from '@chakra-ui/system'
import { Link } from 'react-router-dom'
import { useConfigContext } from '../../../../api/contexts/config/ConfigContext'

export const SidebarHeading = () => {
  const config = useConfigContext()
  const logoUrl = useColorModeValue(config?.components.style?.lightLogoUrl, config?.components.style?.darkLogoUrl)
  const FallbackHeading = (
    <Heading my={0} fontSize="3xl" pr={4}>
      {config?.components.app.siteName}
    </Heading>
  )
  return (
    <Link to="/">
      {logoUrl ? <Image maxH={16} maxW={16} src={logoUrl} alt="CMSch" fallback={FallbackHeading} pr={4} /> : FallbackHeading}
    </Link>
  )
}
