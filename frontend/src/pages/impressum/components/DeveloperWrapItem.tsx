import { Flex, HStack, Tag, Text, Image, useColorModeValue, WrapItem, Icon } from '@chakra-ui/react'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { customTheme } from '../../../util/configs/theme.config'
import { Dev } from '../../../api/hooks/useDevelopers'
import { FaPersonBooth, FaUserCircle } from 'react-icons/fa'

type Props = {
  dev: Dev
}

export const DeveloperWrapItem = ({ dev: { name, img, tags } }: Props) => {
  return (
    <WrapItem border="2px" borderColor={useColorModeValue('gray.200', 'gray.700')} borderRadius="md">
      <Flex direction="column" align="center" w="20rem">
        <Text fontSize="2xl">{name}</Text>
        <Image
          src={img}
          h="15rem"
          fallbackSrc={`${API_BASE_URL}/img/big_pear_logo.png`}
          fallback={<Icon as={FaUserCircle} w="10rem" h="10rem"></Icon>}
        />
        <HStack spacing={2} my={2}>
          {tags.map((tag) => (
            <Tag size={'md'} variant="solid" fontWeight="bold" color="white" bgColor={customTheme.colors.kirDev} key={tag}>
              {tag}
            </Tag>
          ))}
        </HStack>
      </Flex>
    </WrapItem>
  )
}
