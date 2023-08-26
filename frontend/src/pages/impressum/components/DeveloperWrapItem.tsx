import { Flex, HStack, Image, Tag, Text } from '@chakra-ui/react'
import { Dev } from '../../../api/hooks/developers/useDevelopers'
import { API_BASE_URL } from '../../../util/configs/environment.config'
import { customTheme } from '../../../util/configs/theme.config'

type Props = {
  dev: Dev
}

export const DeveloperWrapItem = ({ dev: { name, img, tags } }: Props) => {
  return (
    <Flex direction="column" align="center" w="20rem" h="20rem">
      <Text fontSize="2xl">{name}</Text>
      <Image src={img} h="15rem" fallbackSrc={`${API_BASE_URL}/img/big_pear_logo.png`} />
      <HStack spacing={2} my={2}>
        {tags.map((tag) => (
          <Tag size={'md'} variant="solid" fontWeight="bold" color="white" bgColor={customTheme.colors.kirDev} key={tag}>
            {tag}
          </Tag>
        ))}
      </HStack>
    </Flex>
  )
}
