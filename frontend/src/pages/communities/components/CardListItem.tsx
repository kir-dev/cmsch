import { ChevronRightIcon } from '@chakra-ui/icons'
import { Box, Heading, HStack, Image, Spacer, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { Link } from 'react-router'
import type { Organization } from '../../../util/views/organization'

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
      <Box
        borderRadius="lg"
        padding={{ base: 3, md: 4 }}
        backgroundColor={useColorModeValue('gray.100', 'gray.700')}
        marginTop={{ base: 3, md: 5 }}
        transition="transform .2s ease-in-out"
        _hover={{ transform: 'translateX(0.5em)' }}
      >
        <HStack spacing={{ base: 3, md: 4 }}>
          {logoSource && (
            <Box
              bg="white"
              borderRadius="full"
              padding={{ base: 2, md: 4 }}
              display="flex"
              alignItems="center"
              justifyContent="center"
              flexShrink={0}
            >
              <Image
                src={logoSource}
                alt={data.name}
                minW={{ base: 10, sm: 12, md: 16 }}
                boxSize={{ base: 10, sm: 12, md: 16 }}
                objectFit="contain"
              />
            </Box>
          )}
          <VStack align="flex-start" overflow="hidden" flex={1} spacing={{ base: 0, md: 1 }}>
            <Heading as="h3" size={{ base: 'sm', md: 'md' }} marginY={0} maxWidth="100%" noOfLines={2}>
              {data.name}
            </Heading>
            {data.shortDescription && (
              <Text maxWidth="100%" display={['none', 'none', 'block']} fontSize="sm" noOfLines={2}>
                {data.shortDescription}
              </Text>
            )}
          </VStack>
          <Spacer />
          <ChevronRightIcon boxSize={{ base: 6, md: 10, lg: 16 }} color="gray.300" flexShrink={0} />
        </HStack>
      </Box>
    </Link>
  )
}
