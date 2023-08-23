import { Center, Flex, HStack, Image, Link, Text, useColorModeValue } from '@chakra-ui/react'
import { FaFacebook, FaHeart, FaInstagram } from 'react-icons/fa'
import { CmschContainer } from '../layout/CmschContainer'
import { BUGREPORT_URL } from '../../util/configs/environment.config'
import { customTheme } from '../../util/configs/theme.config'
import Markdown from '../Markdown'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useMemo } from 'react'
import { SupporterLogo } from '../SupporterLogo'
import parseSponsors from './utils/parseSponsors'

export const Footer = () => {
  const config = useConfigContext()
  const component = config?.components.footer
  const sponsors = useMemo(() => parseSponsors(component?.sponsorLogoUrls, component?.sponsorAlts, component?.sponsorWebsiteUrls), [config])
  if (!component) return null
  const sponsorLabelVisible = component?.sponsorsEnabled && sponsors.length > 0
  const contributorsVisible = component?.bmeEnabled || component?.vikEnabled || component?.schonherzEnabled || component?.schdesignEnabled
  return (
    <CmschContainer>
      {component?.footerMessage && (
        <Center flexDirection="column" mx="auto" maxWidth="100%">
          <Markdown text={component?.footerMessage} />
        </Center>
      )}
      {sponsorLabelVisible && (
        <Text textAlign="center" mb={3} mt={10}>
          Támogatóink
        </Text>
      )}
      {component?.sponsorsEnabled && sponsors.length > 0 && (
        <Flex justifyContent="center" alignItems="center" flexWrap="wrap">
          {sponsors.map((sp) => (
            <Link href={sp.url} m={5} key={sp.image}>
              <Image src={sp.image} alt={sp.alt} maxHeight={40} maxWidth={40} />
            </Link>
          ))}
        </Flex>
      )}
      {contributorsVisible && (
        <>
          <Text textAlign="center" mb={3} mt={10}>
            Közreműködők
          </Text>
          <HStack gap={3} mt={3} justifyContent={'center'} alignItems="center" flexWrap="wrap">
            {component.bmeEnabled && <SupporterLogo name="bme" />}
            {component.vikEnabled && <SupporterLogo name="vik" />}
            {component.schonherzEnabled && <SupporterLogo name="schonherz" />}
            {component.schdesignEnabled && <SupporterLogo name="schdesign" />}
          </HStack>
        </>
      )}
      <Flex mt={10} justify="center" align="center" flexDirection={['column', null, 'row']}>
        <Flex align="center" flexDirection="column" justifyContent="center" mb={10} mx={10}>
          <Image src={component?.hostLogo} maxW={40} maxH={40} my={3} alt={component?.hostAlt} />
          {component?.hostWebsiteUrl && (
            <Link
              isExternal
              fontSize="xl"
              _hover={{ color: customTheme.colors.brand, textDecorationLine: 'underline' }}
              href={component?.hostWebsiteUrl}
            >
              Weboldal
            </Link>
          )}
          <HStack>
            {component?.facebookUrl && (
              <Link href={component?.facebookUrl}>
                <FaFacebook size={25} />
              </Link>
            )}
            {component?.instagramUrl && (
              <Link href={component?.instagramUrl}>
                <FaInstagram size={25} />
              </Link>
            )}
          </HStack>
        </Flex>
        <Flex align="center" flexDirection="column" justifyContent="center" mb={10} mx={10}>
          <Flex align="center">
            <Text mr={2}>Made with</Text>
            <FaHeart color="red" size="1.5rem" />
            <Text ml={2}>by</Text>
          </Flex>
          <Image src={useColorModeValue('/img/kirdev.svg', '/img/kirdev-white.svg')} maxW={40} maxH={40} my={3} />
          <HStack align="center">
            {component?.devWebsiteUrl && (
              <Link
                isExternal
                fontSize="xl"
                _hover={{ color: customTheme.colors.brand, textDecorationLine: 'underline' }}
                href={component?.devWebsiteUrl}
              >
                Weboldal
              </Link>
            )}
            <Text>|</Text>
            <Link
              isExternal
              fontSize="xl"
              _hover={{ color: customTheme.colors.kirDev, textDecorationLine: 'underline' }}
              href={BUGREPORT_URL}
            >
              Kapcsolat
            </Link>
          </HStack>
        </Flex>
      </Flex>
    </CmschContainer>
  )
}
