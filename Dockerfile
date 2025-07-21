FROM php:8.2-apache

# Install required PHP extensions
RUN docker-php-ext-install pdo pdo_mysql

# Enable Apache mod_rewrite
RUN a2enmod rewrite

# Set the document root to /var/www/html/public
ENV APACHE_DOCUMENT_ROOT=/var/www/html/public

# Update the Apache config
RUN sed -ri -e 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache2/sites-available/000-default.conf /etc/apache2/apache2.conf /etc/apache2/sites-enabled/000-default.conf

# Copy all files
COPY . /var/www/html

# Set correct permissions (optional but recommended)
RUN chown -R www-data:www-data /var/www/html

# Start Apache
CMD ["apache2-foreground"]
