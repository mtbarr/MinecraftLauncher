FROM nginx:1-alpine

EXPOSE 80

COPY ./server /usr/share/nginx/html
