import { Divider, GridItem, Heading, Text, Link as ChakraLink, Box, HStack, Icon } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { stringifyTimeStamp } from '../../../util/core-functions.util'
import { NewsArticleView } from '../../../util/views/news.view'
import { AbsolutePaths } from '../../../util/paths'
import Markdown from '../../../common-components/Markdown'
import { ExternalLinkIcon, StarIcon } from '@chakra-ui/icons'
import { BsExclamationCircle } from 'react-icons/bs'
import { FaStar } from 'react-icons/fa'

interface NewsListItemProps {
  news: NewsArticleView
  fontSize: string
  index: Number
}

const NewsListItem = ({ news, fontSize, index }: NewsListItemProps) => {
  return (
    <GridItem borderRadius="base" borderColor="whiteAlpha.200" borderWidth="1px" p={4}>
      <Link to={`${AbsolutePaths.NEWS}/${news.url}`}>
        <HStack justifyContent="space-between">
          <Box fontSize="sm" mb={2} fontWeight={300}>
            Közzétéve: {stringifyTimeStamp(news.timestamp)}
          </Box>
          {news.highlighted && (
            <Box>
              <Icon as={FaStar} color="brand.100" w={8} h={8} />
            </Box>
          )}
        </HStack>
        <Heading size={fontSize} my={2}>
          {news.title}
        </Heading>
        {news.briefContent && <Markdown text={news.briefContent} />}
      </Link>
    </GridItem>
  )
}

export default NewsListItem
