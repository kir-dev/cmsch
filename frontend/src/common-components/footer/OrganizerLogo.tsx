import { Divider, HStack, Image, Link, VStack } from '@chakra-ui/react'
import { FaFacebook, FaInstagram } from 'react-icons/fa'

interface OrganizerLogoProps {
  imageSrc: string
  websiteUrl?: string
  facebookUrl?: string
  instagramUrl?: string
  contactUrl?: string
  minimalistic?: boolean
}

export function OrganizerLogo({ imageSrc, minimalistic, websiteUrl, contactUrl, instagramUrl, facebookUrl }: OrganizerLogoProps) {
  return (
    <HStack align="center">
      <Image src={imageSrc} w={32} h={32} objectPosition="center" objectFit="contain" />
      {!minimalistic && (
        <>
          <Divider orientation="vertical" h={20} />
          <VStack align="flex-start">
            {websiteUrl && (
              <Link isExternal fontSize="xl" _hover={{ color: 'brand.500', textDecorationLine: 'underline' }} href={websiteUrl}>
                Weboldal
              </Link>
            )}
            {contactUrl && (
              <Link isExternal fontSize="xl" _hover={{ color: 'brand.500', textDecorationLine: 'underline' }} href={contactUrl}>
                Kapcsolat
              </Link>
            )}
            {(facebookUrl || instagramUrl) && (
              <HStack>
                {facebookUrl && (
                  <Link href={facebookUrl}>
                    <FaFacebook size={25} />
                  </Link>
                )}
                {instagramUrl && (
                  <Link href={instagramUrl}>
                    <FaInstagram size={25} />
                  </Link>
                )}
              </HStack>
            )}
          </VStack>
        </>
      )}
    </HStack>
  )
}
