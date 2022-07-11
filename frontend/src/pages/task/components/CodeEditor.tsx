import Editor from 'react-simple-code-editor'
import { Grammar, highlight, languages } from 'prismjs'
import 'prismjs/components/prism-c'
import 'prismjs/components/prism-cpp'
import 'prismjs/components/prism-csharp'
import 'prismjs/components/prism-java'
import 'prismjs/components/prism-javascript'
import 'prismjs/components/prism-kotlin'
import 'prismjs/components/prism-typescript'
import 'prismjs/components/prism-sql'
import 'prismjs/components/prism-python'
import 'prismjs/themes/prism-okaidia.css'
import { Box, Flex, Select, Stack } from '@chakra-ui/react'
import { codeLanguage } from '../../../util/views/task.view'
import { useState } from 'react'

interface CodeEditorProps {
  code: string
  setCode: React.Dispatch<React.SetStateAction<string>>
  readonly: boolean
}

const LANGUAGE_GRAMMAR_MAP = new Map<string, Grammar>([
  [codeLanguage.C, languages.c],
  [codeLanguage.CPP, languages.cpp],
  [codeLanguage.CSHARP, languages.csharp],
  [codeLanguage.JAVA, languages.java],
  [codeLanguage.JAVASCRIPT, languages.javascript],
  [codeLanguage.TYPESCRIPT, languages.typescript],
  [codeLanguage.SQL, languages.sql],
  [codeLanguage.KOTLIN, languages.kotlin],
  [codeLanguage.PYTHON, languages.python]
])

const CodeEditor = ({ code, setCode, readonly }: CodeEditorProps) => {
  const [selectedLanguage, setSelectedLanguage] = useState<string>(codeLanguage.C)
  return (
    <Stack mt={5}>
      <Flex justify="flex-end">
        <Select onChange={(e) => setSelectedLanguage(e.target.value)} w="10rem" value={selectedLanguage}>
          <option value={codeLanguage.C}>C</option>
          <option value={codeLanguage.CPP}>C++</option>
          <option value={codeLanguage.CSHARP}>C#</option>
          <option value={codeLanguage.JAVA}>JAVA</option>
          <option value={codeLanguage.JAVASCRIPT}>JavaScript</option>
          <option value={codeLanguage.TYPESCRIPT}>TypeScript</option>
          <option value={codeLanguage.SQL}>SQL</option>
          <option value={codeLanguage.KOTLIN}>Kotlin</option>
          <option value={codeLanguage.PYTHON}>Python</option>
        </Select>
      </Flex>

      <Box my={5}>
        <Editor
          value={code}
          onValueChange={(code) => setCode(code)}
          highlight={(code) => highlight(code, LANGUAGE_GRAMMAR_MAP.get(selectedLanguage)!!, selectedLanguage)}
          padding={10}
          readOnly={readonly}
          style={{
            fontFamily: '"Fira code", "Fira Mono", monospace',
            fontSize: 14,
            borderRadius: '0.375rem',
            backgroundColor: '#272822',
            color: 'white'
          }}
        />
      </Box>
    </Stack>
  )
}

export default CodeEditor
