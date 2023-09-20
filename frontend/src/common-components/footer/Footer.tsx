import { Box, Flex, Heading, Image, Text, useColorModeValue } from '@chakra-ui/react'
import { useMemo } from 'react'
import { FaHeart } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { BUGREPORT_URL } from '../../util/configs/environment.config'
import Markdown from '../Markdown'
import { OrganizerLogo } from './OrganizerLogo'
import { PartnerLogo } from './PartnerLogo'
import parseSponsors from './utils/parseSponsors'

const bgShadowColor = '#00000025'

export const Footer = () => {
  const config = useConfigContext()
  const component = config?.components.footer
  const sponsors = useMemo(() => parseSponsors(component?.sponsorLogoUrls, component?.sponsorAlts, component?.sponsorWebsiteUrls), [config])
  if (!component) return null
  const partnersVisible = component?.bmeEnabled || component?.vikEnabled || component?.schonherzEnabled || component?.schdesignEnabled
  const topBarVisible = (component?.sponsorsEnabled || partnersVisible) && !component.minimalisticFooter
  return (
    <Flex flexDirection="column" align="center" w="full" bg={useColorModeValue('lightContainerBg', 'darkContainerBg')}>
      {topBarVisible && (
        <Flex justify="center" w="full" bg={bgShadowColor} p={5}>
          <Flex
            maxWidth={['100%', '64rem']}
            w="full"
            justify={['flex-start', null, 'space-evenly']}
            flexDirection={['column', null, 'row']}
          >
            {component?.sponsorsEnabled && sponsors.length > 0 && (
              <Box w={['full', null, '50%']}>
                <Heading textAlign="center" mb={3} mt={0}>
                  Támogatóink
                </Heading>
                <Flex justifyContent={'center'} alignItems="center" flexWrap="wrap">
                  {sponsors.map((sp, index) => (
                    <a href={sp.url} key={index} target="_blank" referrerPolicy="origin">
                      <Image m={5} src={sp.image} alt={sp.alt} maxH={20} maxW={32} />
                    </a>
                  ))}
                </Flex>
              </Box>
            )}
            {partnersVisible && (
              <Box w={['full', null, '50%']}>
                <Heading textAlign="center" mb={3} mt={[10, null, 0]}>
                  Partnereink
                </Heading>
                <Flex justifyContent={'center'} alignItems="center" flexWrap="wrap">
                  {component.bmeEnabled && <PartnerLogo name="bme" />}
                  {component.vikEnabled && <PartnerLogo name="vik" />}
                  {component.schonherzEnabled && <PartnerLogo name="schonherz" />}
                  {component.schdesignEnabled && <PartnerLogo name="schdesign" />}
                </Flex>
              </Box>
            )}
          </Flex>
        </Flex>
      )}
      <Flex
        px={10}
        py={5}
        gap={5}
        align="center"
        maxWidth={['100%', '64rem']}
        w="full"
        justify="space-between"
        flexDirection={['column', null, 'row']}
      >
        {component?.footerMessage && <Markdown text={component?.footerMessage} />}

        <Flex justify="center" gap={5} align="center" flexDirection={['column', null, 'row']}>
          <OrganizerLogo
            imageSrc={component?.hostLogo}
            websiteUrl={component?.hostWebsiteUrl}
            facebookUrl={component?.facebookUrl}
            instagramUrl={component?.instagramUrl}
            minimalistic={component?.minimalisticFooter}
          />
          <OrganizerLogo
            imageSrc={useColorModeValue('/img/kirdev.svg', '/img/kirdev-white.svg')}
            websiteUrl={component.devWebsiteUrl}
            contactUrl={BUGREPORT_URL}
            minimalistic={component.minimalisticFooter}
          />
        </Flex>
      </Flex>
      <Text w="full" textAlign="center" p={3} bg={bgShadowColor}>
        Made with <FaHeart style={{ display: 'inline' }} color="red" size="1rem" /> by Kir-Dev <br /> Minden jog fenntartva. &copy;{' '}
        {new Date().getFullYear()}
      </Text>
    </Flex>
  )
}
