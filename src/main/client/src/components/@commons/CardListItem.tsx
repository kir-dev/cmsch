import React from 'react'
import { Box, Heading, HStack, VStack, Image, Text, Spacer } from '@chakra-ui/react'
import { Organization } from '../../types/Organization'
import { Link } from 'react-router-dom'
import { ChevronRightIcon } from '@chakra-ui/icons'

type CardListItemProps = {
  data: Organization
  link: string
}

export const CardListItem: React.FC<CardListItemProps> = ({ data, link }) => {
  return (
    <Link to={link}>
      <Box
        borderRadius="lg"
        padding={4}
        boxShadow="md"
        backgroundColor="gray.100"
        marginTop={5}
        transition="transform .2s ease-in-out"
        _hover={{ transform: 'translateX(0.5em)' }}
        color="black"
      >
        <HStack spacing={4}>
          {data.logo && <Image src={data.logo} alt={data.name} boxSize={16} objectFit="contain" />}
          <VStack align="flex-start" overflow="hidden">
            <Heading as="h3" size="md" marginY={0} isTruncated maxWidth="100%">
              {data.name}
            </Heading>
            {data.shortDescription && (
              <Text isTruncated maxWidth="100%" display={['none', 'block']}>
                {data.shortDescription}
              </Text>
            )}
          </VStack>
          <Spacer />
          <ChevronRightIcon boxSize={16} color="gray.300" />
        </HStack>
      </Box>
    </Link>
  )
}
